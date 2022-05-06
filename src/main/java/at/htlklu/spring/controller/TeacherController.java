package at.htlklu.spring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value ="/mvc/teachers")
public class TeacherController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(TeacherController.class);

	private static final String CLASS_NAME = "TeacherController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "TeacherList";

}
