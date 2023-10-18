package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.DepartmentRepository;
import at.htlklu.spring.repository.SchoolClassRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// localhost:8082/mvc/schoolclasses
@Controller
@RequestMapping(value ="mvc/schoolclasses")
public class SchoolClassController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(SchoolClassController.class);
	private static final String CLASS_NAME = "SchoolClassController";
	public static final String FORM_NAME_SINGLE = "SchoolClassSingle";
	public static final String FORM_NAME_LIST = "SchoolClassList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	SchoolClassRepository schoolClassRepository;
	//endregion

	// localhost:8082/mvc/schoolclass
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(SchoolClassController.FORM_NAME_LIST);									// Übergabe der View

		List<SchoolClass> schoolClasses = schoolClassRepository.findAll();			// Sortierung fehlt
		mv.addObject("schoolClasses", schoolClasses);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}

	@GetMapping("{schoolClassId}/students")
	public ModelAndView showStudents(@PathVariable int schoolClassId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showStudents", String.format("%d", schoolClassId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentController.FORM_NAME_LIST);
		Optional<SchoolClass> optSchoolClass = schoolClassRepository.findById(schoolClassId);

		if (optSchoolClass.isPresent()) // Teacher wurde gefunden, weil Id in Tabelle vorhanden
		{
			SchoolClass schoolClass = optSchoolClass.get();
			List<Student> students = schoolClass.getStudents().stream().sorted(Student.BY_SURNAME_FIRSTNAME).collect(Collectors.toList());

			mv.addObject("schoolClass", schoolClass);
			mv.addObject("students", students);
		}
		else
		{
			// Fehlerhandling
		}

		return mv;
	}

	@GetMapping("{schoolClassId}/events")
	public ModelAndView showEvents(@PathVariable int schoolClassId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showEvents", String.format("%d", schoolClassId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(EventController.FORM_NAME_LIST);
		Optional<SchoolClass> optSchoolClass = schoolClassRepository.findById(schoolClassId);

		if (optSchoolClass.isPresent()) // Teacher wurde gefunden, weil Id in Tabelle vorhanden
		{
			SchoolClass schoolClass = optSchoolClass.get();
			//List<Student> students = schoolClass.getStudents().stream().sorted(Student.BY_SURNAME_FIRSTNAME).collect(Collectors.toList());
			List<Event> events = schoolClass.getEvents().stream().sorted(Event.BY_DATE_FROM).collect(Collectors.toList());
			mv.addObject("schoolClass", schoolClass);
			mv.addObject("events", events);
		}
		else
		{
			// Fehlerhandling
		}

		return mv;
	}
}
