package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Subject;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.SubjectRepository;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

// localhost:8082/mvc/subjects
@Controller
@RequestMapping(value ="mvc/subjects")
public class SubjectController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(SubjectController.class);
	private static final String CLASS_NAME = "SubjectController";
	public static final String FORM_NAME_SINGLE = "SubjectSingle";
	public static final String FORM_NAME_LIST = "SubjectList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	SubjectRepository subjectRepository;
	//endregion

	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(SubjectController.FORM_NAME_LIST);									// Übergabe der View

		List<Subject> subjects = subjectRepository.findAll();			// Sortierung fehlt
		mv.addObject("subjects", subjects);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}
}
