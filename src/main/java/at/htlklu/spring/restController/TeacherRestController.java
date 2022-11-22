package at.htlklu.spring.restController;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Department;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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


	//region Get
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

	// http://localhost:8082/teachers
	@GetMapping(value = "")
	public ResponseEntity<?> getAll()
	{
		List<Teacher> teachers = teacherRepository.findAll();
		teachers.stream().forEach(teacher -> TeacherRestController.addLinks(teacher));

		ResponseEntity<List<Teacher>> result = new ResponseEntity<>(teachers, HttpStatus.OK);
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
			TeacherRestController.addLinks(teacher);
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
		}
		else																	// Teacher konnte nicht geladen werden
		{
			result = new ResponseEntity<String>(String.format("Teacher mit Id=%d wurde nicht gefunden.", teacherId),
												HttpStatus.NOT_FOUND);
		}

		return result;
	}

	// http://localhost:8082/teachers/get?teacherId=86
	@GetMapping(value = "get")
	public ResponseEntity<?> getByIdRP(@RequestParam Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getByIdRP", String.format("%d", teacherId)));

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


	// http://localhost:8082/teachers/86/schoolClasses
	@GetMapping(value = "{teacherId}/schoolClasses")
	public ResponseEntity<?> getSchoolClassesByIdPV(@PathVariable Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getSchoolClassesByIdPV", String.format("%d", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);	// lade Teacher
		if (optTeacher.isPresent())												// Teacher konnte geladen werden
		{
			Teacher teacher = optTeacher.get();									// Teacher ermitteln
			Set<SchoolClass> schoolClasses = teacher.getSchoolClasses();		// Menge der Schulklassen vom Teacher ermitteln
			result = new ResponseEntity<Set<SchoolClass>>(schoolClasses, HttpStatus.OK);	// Menge zurückgeben
		}
		else																	// Teacher konnte nicht geladen werden
		{
			result = new ResponseEntity<String>(String.format("Teacher mit Id=%d wurde nicht gefunden.", teacherId), HttpStatus.NOT_FOUND);
		}

		return result;
	}

	// http://localhost:8082/teachers/86/departments
	@GetMapping(value = "{teacherId}/departments")
	public ResponseEntity<?> getDepartmentsByIdPV(@PathVariable Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "getDepartmentsByIdPV", String.format("%d", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);	// lade Teacher
		if (optTeacher.isPresent())												// Teacher konnte geladen werden
		{
			Teacher teacher = optTeacher.get();									// Teacher ermitteln
			Set<Department> departments = teacher.getDepartments();				// Menge der Abteilungen vom Teacher ermitteln
			result = new ResponseEntity<Set<Department>>(departments, HttpStatus.OK);	// Menge zurückgeben
		}
		else																	// Teacher konnte nicht geladen werden
		{
			result = new ResponseEntity<String>(String.format("Teacher mit Id=%d wurde nicht gefunden.", teacherId), HttpStatus.NOT_FOUND);
		}

		return result;
	}

	//endregion


	//region Post: -> Einfügen
	// http://localhost:8082/teachers + Daten im Body mitübergeben
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

		if (!error)											// wenn kein Fehler auftrat
		{
			try
			{
				teacherRepository.save(teacher);			// speichern -> insert
			}
			catch (Exception e)
			{
				e.printStackTrace();
				error = true;
				errorMessage = e.getCause().getCause().getLocalizedMessage();
			}
		}

		ResponseEntity<?> result;
 		if (!error)											// speichern (insert) war erfolgreich
		{
			TeacherRestController.addLinks(teacher);
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
		}
		else
		{
			result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}
	//endregion


	//region Put: -> Einfügen
	// http://localhost:8082/teachers + Daten im Body mitübergeben
	@PutMapping(value = "")
	public ResponseEntity<?> edit(@Valid @RequestBody Teacher teacher,
								  BindingResult bindingResult)
	{
		logger.info(LogUtils.info(CLASS_NAME, "edit", String.format("%s", teacher)));

		boolean error = false;
		String errorMessage = "";

		if (!error)
		{
			error = bindingResult.hasErrors();				// Überprüfung bzgl. der Annotationen
			errorMessage = bindingResult.toString();
		}

		if (!error)											// wenn kein Fehler auftrat
		{
			try
			{
				teacherRepository.save(teacher);			// speichern -> insert
			}
			catch (Exception e)
			{
				e.printStackTrace();
				error = true;
				errorMessage = e.getCause().getCause().getLocalizedMessage();
			}
		}

		ResponseEntity<?> result;
 		if (!error)											// speichern (insert) war erfolgreich
		{
			TeacherRestController.addLinks(teacher);
			result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
		}
		else
		{
			result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}
	//endregion


	//region Delete: -> löschen
	//http://localhost:8082/teachers/86
	@DeleteMapping(value = "{teacherId}")
	public ResponseEntity<?> delete(@PathVariable Integer teacherId)
	{
		logger.info(LogUtils.info(CLASS_NAME, "delete", String.format("%d", teacherId)));

		ResponseEntity<?> result;
		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);	// lade Teacher
		if (optTeacher.isPresent())												// Teacher konnte geladen werden
		{
			Teacher teacher = optTeacher.get();									// hole Teacher aus "Box"
			try
			{
				teacherRepository.delete(teacher);								// versuche Teacher zu löschen
				TeacherRestController.addLinks(teacher);						// ???
				result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);	// erfolgreich, gib Teacher zurück
			}
			catch (Exception e)
			{
				e.printStackTrace();											// Fehler trat auf
				String errorMessage = e.getCause().getCause().getLocalizedMessage();
				result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else																	// Teacher konnte nicht geladen werden
		{
			result = new ResponseEntity<String>(String.format("Teacher mit Id=%d wurde nicht gefunden.", teacherId), HttpStatus.NOT_FOUND);
		}

		return result;
	}
	//endregion


	private static void addLinks(Teacher teacher)
	{
		teacher.add(linkTo(methodOn(TeacherRestController.class)
			   .getByIdPV(teacher.getTeacherId()))
			   .withSelfRel());

		teacher.add(linkTo(methodOn(TeacherRestController.class)
			   .getAll())
			   .withRel("all"));

		teacher.add(linkTo(methodOn(TeacherRestController.class)
			   .getSchoolClassesByIdPV(teacher.getTeacherId()))
			   .withRel("schoolClasses"));

		teacher.add(linkTo(methodOn(TeacherRestController.class)
			   .getDepartmentsByIdPV(teacher.getTeacherId()))
			   .withRel("departments"));
	}
}
