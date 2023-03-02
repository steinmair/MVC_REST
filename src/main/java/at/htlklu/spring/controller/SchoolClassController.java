package at.htlklu.spring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value ="glf9/schoolClasses")
public class SchoolClassController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(SchoolClassController.class);
	private static final String CLASS_NAME = "SchoolClassController";
	public static final String FORM_NAME_SINGLE = "SchoolClassSingle";
	public static final String FORM_NAME_LIST = "SchoolClassList";
	//endregion
}
