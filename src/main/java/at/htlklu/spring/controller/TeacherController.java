package at.htlklu.spring.controller;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.*;

import at.htlklu.spring.api.LogUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Binding;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// localhost:8082/mvc/teachers
// localhost:8082/mvc/teachers/1/departments
// localhost:8082/mvc/teachers/1/schoolClasses
@Controller
@RequestMapping(value ="mvc/teachers")
public class TeacherController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(TeacherController.class);
	private static final String CLASS_NAME = "TeacherController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "TeacherList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	TeacherRepository teacherRepository;
	//endregion

	//region Show
	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(TeacherController.FORM_NAME_LIST);				// Übergabe der View

		List<Teacher> teachers = teacherRepository.findAll();			// Sortierung fehlt
		mv.addObject("teachers", teachers);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}

	// localhost:8082/mvc/teachers/105/departments
	@GetMapping("{teacherId}/departments")
	public ModelAndView showDepartments(@PathVariable int teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showDepartments", String.format("%d", teacherId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(DepartmentController.FORM_NAME_LIST);
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

		if (optTeacher.isPresent()) // Teacher wurde gefunden, weil Id in Tabelle vorhanden
		{
			Teacher teacher = optTeacher.get();
			//Set<Department> departments = teacher.getDepartments();
			List<Department> departments = teacher.getDepartments()
					.stream()
					.sorted(Department.BY_NAME)
					.collect(Collectors.toList());

			mv.addObject("teacher", teacher);
			mv.addObject("departments", departments);
		}
		else
		{
			// Fehlerhandling
		}

		return mv;
	}

	@GetMapping("{teacherId}/schoolClasses")
	public ModelAndView showSchoolClasses(@PathVariable int teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showSchoolClasses", String.format("%d", teacherId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(SchoolClassController.FORM_NAME_LIST);
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

		if (optTeacher.isPresent()) // Teacher wurde gefunden, weil Id in Tabelle vorhanden
		{
			Teacher teacher = optTeacher.get();
			List<SchoolClass> schoolClasses = teacher.getSchoolClasses()
					.stream()
					.sorted(SchoolClass.BY_NAME)
					.collect(Collectors.toList());

			mv.addObject("teacher", teacher);
			mv.addObject("schoolClasses", schoolClasses);
		}
		else
		{
			// Fehlerhandling
		}

		return mv;
	}
	//endregion

	//region Add und Edit Variante 1
	// localhost:8082/mvc/teachers/add
@GetMapping("add")
	public ModelAndView add()
	{
		logger.info(LogUtils.info(CLASS_NAME, "add"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(TeacherController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben

		Teacher teacher = new Teacher();

		mv.addObject("teacher",teacher);

	    return mv;
	}

// localhost:8082/mvc/teachers/edit/79
@GetMapping("edit/{teacherId}")
	public ModelAndView edit(@PathVariable int teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "edit", String.format("%d", teacherId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(TeacherController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben

		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

		if (optTeacher.isPresent()){
			Teacher teacher = optTeacher.get();
			mv.addObject("teacher",teacher);
		}else {
			// to do: error handling
		}

		Teacher teacher = new Teacher();



	    return mv;
	}
	//endregion

	//region Add und Edit Variante 2

	// localhost:8082/mvc/teachers/addEdit
	// localhost:8082/mvc/teachers/addEdit/79
@GetMapping({"addEdit","addEdit/{optTeacherId}"})
	public ModelAndView addEdit(@PathVariable Optional<Integer> optTeacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "addEdit", String.format("%s", optTeacherId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(TeacherController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben

		int teacherId = optTeacherId.orElse(-1);	// wenn in der URL die teacherId übergeben wurde, dann wird diese in der Variable teacherId gespeichert
														// wenn in der URL keine teacherId übergeben wurde, dann wird "-1" in der teacherId gespeichert

		Teacher teacher = teacherRepository.findById(teacherId).orElse(new Teacher());

		mv.addObject("teacher",teacher);

	    return mv;
	}
	//endregion

	// localhost:8082/mvc/teachers/save
	// @Valid bedeutet, dass die Attribute vom Techer gegen die Regeln in der Klasse Teacher validiert werden
	// Beispiel für das Atrribut Surname
	// @NotBlank: der Surname darf nicht leer sein oder aus nur Leerzeichen bestehen
	//@Length(min = 3, max = 25)= die Länge des Surnames muss zwischen 3 un d25 zeichen lang sein
	//private String surname;
@PostMapping("save")
	public ModelAndView save(@Valid  @ModelAttribute Teacher teacher,
							 BindingResult bindingResult)
	{
		logger.info(LogUtils.info(TeacherController.class.getSimpleName(), "save", String.format("%s", teacher)));

		boolean error = false;
		ModelAndView mv = new ModelAndView();

		if (!error){	// wenn kein Fehler (ist immer true)
			error = bindingResult.hasErrors();	// im binding Result sind Fehler gespeichert die beim validieren vom Teacher hinsichtlich der Anotationen auftreten
		}

		error= bindingResult.hasErrors();	// im binding Result sind Fehler gespeichert die beim validieren vom Teacher hinsichtlich der Anotationen auftreten
		if (!error){	// wenn kein Fehler dann speichern
			try {
				teacherRepository.save(teacher);	// Datensatz speichern
			}catch (Exception e){					// Beispiel: Datenbank nicht verfügbar
													// DB Contraints violated

				// Mögliche Fehler, die autreten könnten
				// kein Netzwerk
				// keine Berechtigung
				// ShortName nicht eindeutig
				error = true;
				logger.info(LogUtils.info(CLASS_NAME,"save_save", ErrorsUtils.getErrorMessage(e)));

				bindingResult.addError(new ObjectError("globalError",ErrorsUtils.getErrorMessage(e)));

				}
			}
		if (!error){
			mv.setViewName("redirect:/mvc/teachers");
		}else {
			// die Objekte "teacher" (wegen der Annotation @ModelAttribute) und
			// "bindingResult" werden automatisch als Attribute beim Model "mv" hinzugefügt (add)
			// d.h. die Aufrufe mv.addObject("teacher", teacher); und mv.addObject("bindingResult", bindingResult);
			// sind nicht notwendig und sinnvoll
			mv.setViewName(TeacherController.FORM_NAME_SINGLE); // sonst öffne wieder den fehlerhaften Lehrer und es werden die Fehlermeldungen
		}

		//mv.setViewName(TeacherController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben
		//mv.addObject("teacher",teacher);

	    return mv;
	}
}
