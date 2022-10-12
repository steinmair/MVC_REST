package at.htlklu.spring.restController;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("teachers")
public class TeacherRestController
{
	//region Properties
	private static final Logger logger = LogManager.getLogger(TeacherRestController.class);
	private static final String CLASS_NAME = "TeacherRestController";

	@Autowired
	TeacherRepository teacherRepository;
	//endregion


	// http://localhost:8082/teachers/getRandomA
	@GetMapping(value = "getRandomA")
	public Teacher getRandomA()
	{
		logger.info(LogUtils.info(CLASS_NAME, "getRandomA"));

		Teacher teacher = new Teacher("Mustermann", "Max", "Max" + (int)(Math.random()*10_000), 'm');

		return teacher;
	}

	// http://localhost:8082/teachers/getRandomB
	@GetMapping(value = "getRandomB")
	public ResponseEntity<Teacher> getRandomB()
	{
		logger.info(LogUtils.info(CLASS_NAME, "getRandomA"));

		Teacher teacher = new Teacher("Mustermann", "Max", "Max" + (int)(Math.random()*10_000), 'm');
		ResponseEntity<Teacher> result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);

		return result;
	}

	// http://localhost:8082/teachers/86
	@GetMapping(value = "{teacherId}")
	public ResponseEntity<?> getByIdPV(@PathVariable Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getByIdPV", String.format("%d", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);	// lade Teacher
		if (optTeacher.isPresent())												// Teacher konnte geladen werden
		{
			Teacher teacher = optTeacher.get();
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
		}
		else																	// Teacher konnte nicht geladen werden
		{
			result = new ResponseEntity<String>("Teacher wurde nicht gefunden", HttpStatus.NOT_FOUND);
		}

		return result;
	}



}
