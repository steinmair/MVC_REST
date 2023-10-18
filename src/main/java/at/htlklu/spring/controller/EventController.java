package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Event;
import at.htlklu.spring.model.Subject;
import at.htlklu.spring.repository.EventRepository;
import at.htlklu.spring.repository.SubjectRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

// localhost:8082/mvc/events
@Controller
@RequestMapping(value ="mvc/events")
public class EventController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(EventController.class);
	private static final String CLASS_NAME = "EventController";
	public static final String FORM_NAME_SINGLE = "EventSingle";
	public static final String FORM_NAME_LIST = "EventList";	// das ist die View, Dateiname vom Vorlagefile welches im resource Ordner zu finden ist

	@Autowired
	EventRepository eventRepository;
	//endregion

	// localhost:8082/mvc/events
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(EventController.FORM_NAME_LIST);				// Übergabe der View

		List<Event> events = eventRepository.findAll();				// Sortierung fehlt


		mv.addObject("events", events);					// Übergabe des Models, Der attribute Name wird der for each loop im TeacherList.html (table) übergeben

	    return mv;
	}

}
