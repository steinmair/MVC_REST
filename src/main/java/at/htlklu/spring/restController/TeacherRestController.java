package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.HateoasUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.controller.TeacherController;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Teacher;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("teachers")
public class TeacherRestController extends RepresentationModel {
    private static final Logger logger = LogManager.getLogger(TeacherRestController.class);
    private static final String className = "TeacherRestController";

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SchoolClassRepository schoolClassRepository;

    //http://localhost:8082/teachers/1
    @GetMapping(value = "{teacherId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer teacherId) {
        logger.info(LogUtils.info(className, "getByPV", String.format("(%d)", teacherId)));
        ResponseEntity<?> result;
        Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);


        if (optTeacher.isPresent()) {
            Teacher teacher = optTeacher.get();
            addLinks(teacher);
            result = new ResponseEntity<>(teacher, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Lehrer/in mit der Id = %d nicht vorhanden", teacherId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{teacherId}/schoolclasses")
    public ResponseEntity<?> getByIdSchoolClass(@PathVariable Integer teacherId) {
        logger.info(LogUtils.info(className, "getByIdSchoolClass", String.format("(%d)", teacherId)));
        ResponseEntity<?> result;
        Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

        if (optTeacher.isPresent()) {
            Teacher teacher = optTeacher.get();
            result = new ResponseEntity<>(teacher.getSchoolClasses(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Lehrer/in mit der Id = %d hat keine Schulklasse", teacherId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{teacherId}/departments")
    public ResponseEntity<?> getByIdDepartments(@PathVariable Integer teacherId){
        logger.info(LogUtils.info(className, "getByIdDepartments", String.format("(%d)", teacherId)));
        ResponseEntity<?> result;
        Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

        if (optTeacher.isPresent()){
            Teacher teacher = optTeacher.get();
            result = new ResponseEntity<>(teacher.getDepartments(), HttpStatus.OK);
        }else {
            result = new ResponseEntity<>(String.format("Lehrer mit der Id= %d ist kein Abteilungsvorstand!", teacherId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //region Put and Push
// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Teacher teacher,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "add", String.format("(%s)", teacher)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                teacherRepository.save(teacher);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Teacher teacher,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "update", String.format("(%s)", teacher)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                teacherRepository.save(teacher);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

//endregion

    @DeleteMapping(value = "{teacherId}")
    public ResponseEntity<?> deletePV2(@PathVariable Integer teacherId) {
        logger.info(LogUtils.info(className, "deletePV2", String.format("(%d)", teacherId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Teacher teacher = null;


        if (!error) {
            Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);
            if (optTeacher.isPresent()) {
                teacher = optTeacher.get();
            } else {
                error = true;
                errorMessage = "Teacher not found";
            }
        }

        if (!error) {
            try {
                teacherRepository.delete(teacher);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static void addLinks(Teacher teacher){
        if (HateoasUtils.enableHateoas){
            teacher.add(WebMvcLinkBuilder.linkTo(methodOn(TeacherRestController.class)
                            .getByIdPV(teacher.getTeacherId()))
                            .withSelfRel());
            teacher.add(WebMvcLinkBuilder.linkTo(methodOn(TeacherRestController.class)
                            .getByIdSchoolClass(teacher.getTeacherId()))
                            .withRel("schoolClasses"));
            teacher.add(WebMvcLinkBuilder.linkTo(methodOn(TeacherRestController.class)
                    .getByIdDepartments(teacher.getTeacherId())).withRel("departments"));
        }
    }
}




