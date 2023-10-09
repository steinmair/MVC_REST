package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Teacher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Controller
@RequestMapping(value ="start")
public class StartController
{
    private static Logger logger = LogManager.getLogger(StartController.class);
    private static final String CLASS_NAME = "StartController";


    @GetMapping("test")
	@ResponseBody
	public String test()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));
	    return "test";
	}

//    localhost:8082/start/add/3/5  // Url ca. = Path
    @GetMapping("add/{number1}/{number2}")
    @ResponseBody
    public int addPV(@PathVariable int number1,
                     @PathVariable int number2)
    {
        logger.info(LogUtils.info(CLASS_NAME, "addPV", String.format("%d,%d", number1, number2)));
        return number1 + number2;
    }

//    localhost:8082/start/add?number1=3&number2=5
    @GetMapping("add")
    @ResponseBody
    public int addRP(@RequestParam int number1,
                     @RequestParam int number2)
    {
        logger.info(LogUtils.info(CLASS_NAME, "addRP", String.format("%d,%d", number1, number2)));
        return number1 + number2;
    }


    @GetMapping(value = "getTeacher")
	@ResponseBody
	public Teacher get1a()
	{
		logger.info(LogUtils.info(CLASS_NAME, "get1a", ""));
		Teacher teacher = new Teacher("Mustermann", "Max", "MM", 'm',
									"Mag.", "???", "0...");
		return teacher;
	}

}
