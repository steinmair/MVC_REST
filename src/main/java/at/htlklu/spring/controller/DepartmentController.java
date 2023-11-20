package at.htlklu.spring.controller;

import at.htlklu.spring.api.ErrorsUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
	@Autowired
	TeacherRepository teacherRepository;
	//endregion

	// localhost:8082/mvc/departments
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(DepartmentController.FORM_NAME_LIST);									// Übergabe der View

		// Variante 1 ohne Optimierung
		//List<Department> departments = departmentRepository.findAll();			// Sortierung fehlt

		// Variante 2 mit Optimierung
		List<Department> departments = departmentRepository.findByOrderByNameAsc();			// Sortierung fehlt
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

	@GetMapping({"addEdit","addEdit/{optDepartmentId}"})
	public ModelAndView addEdit(@PathVariable Optional<Integer> optDepartmentId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "addEdit", String.format("%s", optDepartmentId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(DepartmentController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben

		int departmentId = optDepartmentId.orElse(-1);	// wenn in der URL die teacherId übergeben wurde, dann wird diese in der Variable teacherId gespeichert
														// wenn in der URL keine departmentId übergeben wurde, dann wird "-1" in der teacherId gespeichert

		Department department = departmentRepository.findById(departmentId).orElse(new Department());

		//Variante 1:
		//List<Teacher> teachers = teacherRepository.findAll();


//		//Variante 2:
//		List<Teacher> teachers = teacherRepository.findAll()
//				.stream()
//				.sorted(Teacher.BY_SURNAME_FIRSTNAME)
//				.collect(Collectors.toList());
//
		List<Teacher> teachers = teacherRepository.findByOrderBySurnameAscFirstnameAsc();

		mv.addObject("department",department);
		mv.addObject("teachers",teachers);

	    return mv;
	}

	@PostMapping("save") 	// Referenz TEACHER
	public ModelAndView save(@Valid @ModelAttribute Department department,
							 BindingResult bindingResult)
	{
		logger.info(LogUtils.info(DepartmentController.class.getSimpleName(), "save", String.format("%s", department)));

		boolean error = false;
		ModelAndView mv = new ModelAndView();

		if (!error){	// wenn kein Fehler (ist immer true)
			error = bindingResult.hasErrors();
		}

		error= bindingResult.hasErrors();	// im binding Result sind Fehler gespeichert die beim validieren vom Teacher hinsichtlich der Anotationen auftreten
		if (!error){	// wenn kein Fehler dann speichern
			try {
				departmentRepository.save(department);	// Datensatz speichern
			}catch (Exception e){						// Beispiel: Datenbank nicht verfügbar

				error = true;
				logger.info(LogUtils.info(CLASS_NAME,"save_save", ErrorsUtils.getErrorMessage(e)));

				bindingResult.addError(new ObjectError("globalError",ErrorsUtils.getErrorMessage(e)));

				}
			}
		if (!error){
			mv.setViewName("redirect:/mvc/departments");
		}else {
			mv.setViewName(DepartmentController.FORM_NAME_SINGLE);

			List<Teacher> teachers = teacherRepository.findAll()
				.stream()
				.sorted(Teacher.BY_SURNAME_FIRSTNAME)
				.collect(Collectors.toList());
			mv.addObject("teachers", teachers);
		}

	    return mv;
	}

}
