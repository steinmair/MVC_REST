package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.StudentRepository;
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
}
