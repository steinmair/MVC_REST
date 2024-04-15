package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.HateoasUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Event;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Student;
import at.htlklu.spring.repository.SchoolClassRepository;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("schoolclasses")
public class SchoolClassRestController extends RepresentationModel {
    private static final Logger logger = LogManager.getLogger(SchoolClassRestController.class);
    private static final String className = "SchoolClassRestController";

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SchoolClassRepository schoolClassRepository;
    @Autowired
    EventRestController eventRestController;

    //http://localhost:8082/teachers/1
    @GetMapping(value = "")
    public ResponseEntity<?> getAll() {
        ResponseEntity<?> result;
        try {
            logger.info(LogUtils.info(className, "getAllSchoolClasses", "Fetching all school classes"));
            List<SchoolClass> allSchoolClasses = schoolClassRepository.findAll();

            if (!allSchoolClasses.isEmpty()) {
                result = new ResponseEntity<>(allSchoolClasses, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No school classes found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error fetching all school classes: " + e.getMessage());
            result = new ResponseEntity<>("Error fetching all school classes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    @GetMapping(value = "{schoolClassId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer schoolClassId) {
        logger.info(LogUtils.info(className, "getByPV", String.format("(%d)", schoolClassId)));
        ResponseEntity<?> result;
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);


        if (optionalSchoolClass.isPresent()) {
            SchoolClass schoolClass = optionalSchoolClass.get();
            addLinks(schoolClass);
            result = new ResponseEntity<>(schoolClass, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("SchoolClass mit der Id = %d nicht vorhanden", schoolClassId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{schoolClassId}/students")
    public ResponseEntity<?> getByIdStudents(@PathVariable Integer schoolClassId) {
        logger.info(LogUtils.info(className, "getByIdStudents", String.format("(%d)", schoolClassId)));
        ResponseEntity<?> result;
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);

        if (optionalSchoolClass.isPresent()) {
            SchoolClass schoolClass = optionalSchoolClass.get();
            SchoolClassRestController.addLinks(schoolClass);
            Set<Student> students =  schoolClass.getStudents();
            students.forEach(student -> StudentRestController.addLinks(student));
            result = new ResponseEntity<>(students, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("SchoolClass mit der Id = %d hat keine Studenten", schoolClassId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{schoolClassId}/events")
    public ResponseEntity<?> getByIdEvents(@PathVariable Integer schoolClassId){
        logger.info(LogUtils.info(className, "getByIdEvents", String.format("(%d)", schoolClassId)));
        ResponseEntity<?> result;
        Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);

        if (optionalSchoolClass.isPresent()){
            SchoolClass schoolClass = optionalSchoolClass.get();
            SchoolClassRestController.addLinks(schoolClass);
            Set<Event> events =  schoolClass.getEvents();
            events.forEach(event -> EventRestController.addLinks(event));
            result = new ResponseEntity<>(events, HttpStatus.OK);

        }else {
            result = new ResponseEntity<>(String.format("SchoolClass mit der Id= %d hat keine Events!", schoolClassId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //region Put and Push
// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody SchoolClass schoolClass,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "add", String.format("(%s)", schoolClass)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                schoolClassRepository.save(schoolClass);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(schoolClass, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody SchoolClass schoolClass,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "update", String.format("(%s)", schoolClass)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                schoolClassRepository.save(schoolClass);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(schoolClass, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

//endregion

    @DeleteMapping(value = "{schoolClassId}")
    public ResponseEntity<?> deletePV2(@PathVariable Integer schoolClassId) {
        logger.info(LogUtils.info(className, "deletePV2", String.format("(%d)", schoolClassId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        SchoolClass schoolClass = null;


        if (!error) {
            Optional<SchoolClass> optionalSchoolClass = schoolClassRepository.findById(schoolClassId);
            if (optionalSchoolClass.isPresent()) {
                schoolClass = optionalSchoolClass.get();
            } else {
                error = true;
                errorMessage = "SchoolClass not found";
            }
        }

        if (!error) {
            try {
                schoolClassRepository.delete(schoolClass);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<>(schoolClass, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static void addLinks(SchoolClass schoolClass){
        if (HateoasUtils.enableHateoas){
            schoolClass.add(WebMvcLinkBuilder.linkTo(methodOn(SchoolClassRestController.class)
                            .getByIdPV(schoolClass.getSchoolClassId()))
                            .withSelfRel());
            schoolClass.add(WebMvcLinkBuilder.linkTo(methodOn(SchoolClassRestController.class)
                            .getByIdStudents(schoolClass.getSchoolClassId()))
                            .withRel("students"));
            schoolClass.add(WebMvcLinkBuilder.linkTo(methodOn(SchoolClassRestController.class)
                    .getByIdEvents(schoolClass.getSchoolClassId())).withRel("events"));
        }
    }
}




