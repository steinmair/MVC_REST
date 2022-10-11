package at.htlklu.spring.controller;

import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.*;

import at.htlklu.spring.api.LogUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value ="/mvc/teachers2")
public class Teacher2Controller
{
	//region Properties
	private static Logger logger = LogManager.getLogger(Teacher2Controller.class);
	private static final String CLASS_NAME = "TeacherController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "TeacherList";

	@Autowired
	TeacherRepository teacherRepository;
	//endregion


	// localhost:8082/mvc/teachers2/show
	@GetMapping("/show")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(FORM_NAME_LIST);

		List<Teacher> teachers = teacherRepository.findAll();
		mv.addObject("teachers", teachers);

	    return mv;
	}
}
