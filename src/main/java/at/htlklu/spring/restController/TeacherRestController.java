package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.controller.TeacherController;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teachers")
public class TeacherRestController
{
	//region Properties
	private static final Logger logger = LogManager.getLogger(TeacherRestController.class);
	private static final String CLASS_NAME = "TeacherRestController";

	@Autowired
	TeacherRepository teacherRepository;
	//endregion


	//region Show
	// http://localhost:8082/teachers
	@GetMapping(value = "")
	public List<Teacher> getAll() 
	{
		logger.info(LogUtils.info(CLASS_NAME, "getAll", ""));
		
		List<Teacher> teachers = teacherRepository.findAll();

		return teachers;
	}


	// http://localhost:8082/teachers/getRandomA
	@GetMapping(value = "/getRandomA")
	public Teacher getRandomA()
	{
		logger.info(LogUtils.info(CLASS_NAME, "getRandomA"));

		Teacher teacher = new Teacher("Mustermann", "Max", "Max" + (int)(Math.random()* 10_000), 'm');

		return teacher;
	}


	// http://localhost:8082/teachers/getRandomB
	@GetMapping(value = "/getRandomB")
	public ResponseEntity<?> getRandomB()
	{
		logger.info(LogUtils.info(CLASS_NAME, "getRandomB"));

		Teacher teacher = new Teacher("Mustermann", "Max", "Max" + (int)(Math.random()* 10_000), 'm');
		ResponseEntity<?> result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);

		return result;
	}

	// http://localhost:8082/teachers/getRandomC
	@GetMapping(value = "/getRandomC")
	public ResponseEntity<?> getRandomC()
	{
		logger.info(LogUtils.info(CLASS_NAME, "getRandomC"));

		Teacher teacher = new Teacher("Mustermann", "Max", "Max" + (int)(Math.random()* 10_000), 'm');

		ResponseEntity<?> result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);

		return result;
	}


	
	// http://localhost:8082/teachers/1
	@GetMapping(value = "/{teacherId}")
	public ResponseEntity<?> getByIdPV(@PathVariable Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getByIdPV", String.format("(%d)", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);
		if (optTeacher.isPresent())
		{
			Teacher teacher = optTeacher.get();
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);		// Variante: alt
		}
		else
		{
			result = new ResponseEntity<>(String.format("LehrerIn mit der Id = %d nicht vorhanden", teacherId),
										  HttpStatus.NOT_FOUND);
		}

		return result;
	}

	// http://localhost:8082/teachers/get?teacherId=86
	@GetMapping(value = "/get")
	public ResponseEntity<?> getByIdRP(@RequestParam Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getByIdRP", String.format("(%d)", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);
		if (optTeacher.isPresent())
		{
			Teacher teacher = optTeacher.get();
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);		// Variante: alt
		}
		else
		{
			result = new ResponseEntity<>(String.format("LehrerIn mit der Id = %d nicht vorhanden", teacherId),
										  HttpStatus.NOT_FOUND);
		}

		return result;
	}


	@PostMapping(value = "")
	public ResponseEntity<?> add(@Valid @RequestBody Teacher teacher,
								 BindingResult bindingResult)
	{
		logger.info(LogUtils.info(CLASS_NAME, "add", String.format("%s", teacher)));

		boolean error = false;
		String errorMessage = "";

		if (!error)
		{
			error = bindingResult.hasErrors();				// Überprüfung bzgl. der Annotationen
			errorMessage = bindingResult.toString();
		}

		if (!error)
		{
			try
			{
				teacherRepository.save(teacher);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				error = true;
				errorMessage = e.getCause().getCause().getLocalizedMessage();
			}
		}

		ResponseEntity<?> result;
 		if (!error)
		{
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
		}
		else
		{
			result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

}
