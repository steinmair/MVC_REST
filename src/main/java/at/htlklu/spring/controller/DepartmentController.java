package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Department;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.DepartmentRepository;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// localhost:8082/mvc/departments
@Controller
@RequestMapping(value ="mvc/departments")
public class DepartmentController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(DepartmentController.class);
	private static final String CLASS_NAME = "DepartmentController";
	public static final String FORM_NAME_SINGLE = "DepartmentSingle";
	public static final String FORM_NAME_LIST = "DepartmentList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	DepartmentRepository departmentRepository;
	//endregion

	// localhost:8082/mvc/departments
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(DepartmentController.FORM_NAME_LIST);									// Übergabe der View

		List<Department> departments = departmentRepository.findAll();			// Sortierung fehlt
		mv.addObject("departments", departments);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}
	@GetMapping("{departmentId}/schoolClasses")
	public ModelAndView showSchoolClasses(@PathVariable int departmentId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showSchoolClasses", String.format("%d", departmentId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(SchoolClassController.FORM_NAME_LIST);
		Optional<Department> optDepartment = departmentRepository.findById(departmentId);

		if (optDepartment.isPresent()) // Teacher wurde gefunden, weil Id in Tabelle vorhanden
		{
			Department department = optDepartment.get();
			List<SchoolClass> schoolClasses = department.getSchoolClasses()
					.stream()
					.sorted(SchoolClass.BY_NAME)
					.collect(Collectors.toList());

			mv.addObject("department", department);
			mv.addObject("schoolClasses", schoolClasses);
		}
		else
		{
			// Fehlerhandling
		}

		return mv;
	}

}
