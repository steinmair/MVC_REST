package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.SchoolClassRepository;
import at.htlklu.spring.repository.TeacherRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("teachers")
public class RestUtilitiesController {
    private static final Logger logger = LogManager.getLogger(RestUtilitiesController.class);
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

//    @DeleteMapping(value = "{teacherId}")
//    public ResponseEntity<?> deletePV(@PathVariable Integer teacherId){
//        logger.info(LogUtils.info(className, "deletePV",String.format("(%d)", teacherId)));
//        boolean error = false;
//        String errorMessage = "";
//        ResponseEntity<?> result;
//
//        try {
//            teacherRepository.deleteById(teacherId);
//        } catch (Exception e) {
//            error = true;
//            errorMessage = ErrorsUtils.getErrorMessage(e);
//        }
//
//        if (!error){
//            result = new ResponseEntity<>("Alles super", HttpStatus.OK);
//        }else {
//            // Fehlermeldung
//           result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return result;
//    }

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
}

//        try {
//            Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);
//            teacher = optTeacher.get();
//
//            teacherRepository.deleteById(teacherId);
//
//        } catch (Exception e) {
//            error = true;
//            errorMessage = ErrorsUtils.getErrorMessage(e);
//        }
//
//        if (!error){
//            result = new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
//        }else {
//            // Fehlermeldung
//           result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return result;
//    }



