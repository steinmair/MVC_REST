package at.htlklu.spring.controller;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.StudentRepository;
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

// localhost:8082/mvc/students
// localhost:8082/mvc/students/1/schoolclasses
@Controller
@RequestMapping(value ="mvc/students")
public class StudentController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(StudentController.class);
	private static final String CLASS_NAME = "StudentController";
	public static final String FORM_NAME_SINGLE = "StudentSingle";
	public static final String FORM_NAME_LIST = "StudentList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	StudentRepository studentRepository;
	//endregion

	// localhost:8082/mvc/students
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentController.FORM_NAME_LIST);				// Übergabe der View

		List<Student> students = studentRepository.findAll();			// Sortierung fehlt
		mv.addObject("students", students);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}

		// localhost:8082/mvc/teachers/105/studentSubjects
	@GetMapping("{studentId}/studentSubjects")
	public ModelAndView showStudentSubjects(@PathVariable int studentId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showStudentSubjects", String.format("%d", studentId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentSubjectController.FORM_NAME_LIST);
		Optional<Student> optStudent = studentRepository.findById(studentId);

		if (optStudent.isPresent()) // Student wurde gefunden, weil Id in Tabelle vorhanden
		{
			Student student = optStudent.get();

			List<StudentSubject> studentSubjects = student.getStudentSubjects()
					.stream()
					.collect(Collectors.toList());

			mv.addObject("student", student);
			mv.addObject("studentSubjects", studentSubjects);
		}
		else
		{
			// Fehlerhandling
		}
		return mv;
	}

			// localhost:8082/mvc/teachers/105/departments
	@GetMapping("{studentId}/absences")
	public ModelAndView showAbsences(@PathVariable int studentId)
	{
		logger.info(LogUtils.info(CLASS_NAME,
				"showAbsences", String.format("%d", studentId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentSubjectController.FORM_NAME_LIST);
		Optional<Student> optStudent = studentRepository.findById(studentId);

		if (optStudent.isPresent()) // Student wurde gefunden, weil Id in Tabelle vorhanden
		{
			Student student = optStudent.get();

			List<Absence> absences = student.getAbsences()
					.stream()
					.collect(Collectors.toList());

			mv.addObject("student", student);
			mv.addObject("absences", absences);
		}
		else
		{
			// Fehlerhandling
		}
		return mv;
	}

	@GetMapping({"addEdit","addEdit/{optStudentId}"})
	public ModelAndView addEdit(@PathVariable Optional<Integer> optStudentId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "addEdit", String.format("%s", optStudentId)));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentController.FORM_NAME_SINGLE); 			// hier wird der Fomularname eingegeben

		int studentId = optStudentId.orElse(-1);	// wenn in der URL die studentId übergeben wurde, dann wird diese in der Variable studentId gespeichert
														// wenn in der URL keine studentId übergeben wurde, dann wird "-1" in der studentId gespeichert

		Student student = studentRepository.findById(studentId).orElse(new Student());

		mv.addObject("student",student);

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
	public ModelAndView save(@Valid @ModelAttribute Student student,
							 BindingResult bindingResult)
	{
		logger.info(LogUtils.info(StudentController.class.getSimpleName(), "save", String.format("%s", student)));

		boolean error = false;
		ModelAndView mv = new ModelAndView();

		if (!error){	// wenn kein Fehler (ist immer true)
			error = bindingResult.hasErrors();	// im binding Result sind Fehler gespeichert die beim validieren vom Student hinsichtlich der Anotationen auftreten
		}

		error= bindingResult.hasErrors();	// im binding Result sind Fehler gespeichert die beim validieren vom Teacher hinsichtlich der Anotationen auftreten
		if (!error){	// wenn kein Fehler dann speichern
			try {
				studentRepository.save(student);	// Datensatz speichern
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
			mv.setViewName("redirect:/mvc/students");
		}else {

			mv.setViewName(StudentController.FORM_NAME_SINGLE); // sonst öffne wieder den fehlerhaften Studenten und es werden die Fehlermeldungen
		}

	    return mv;
	}

}
