package at.htlklu.spring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value ="glf9/students")
public class StudentController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(StudentController.class);
	private static final String CLASS_NAME = "StudentController";
	public static final String FORM_NAME_SINGLE = "StudentSingle";
	public static final String FORM_NAME_LIST = "StudentList";
	//endregion
}
