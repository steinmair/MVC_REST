package at.htlklu.spring.restController;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.controller.TeacherController;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.SchoolClassRepository;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("teachers")
public class TeacherRestController {
    private static final Logger logger = LogManager.getLogger(TeacherRestController.class);
    private static final String className = "TeacherRestController";

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SchoolClassRepository schoolClassRepository;
    //http://localhost:8082/teachers/1
    @GetMapping(value = "{teacherId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer teacherId){
        logger.info(LogUtils.info(className, "getByPV",String.format("(%d)", teacherId)));
        ResponseEntity<?> result;
        Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);
        if (optTeacher.isPresent()){
            Teacher teacher = optTeacher.get();
            result = new ResponseEntity<>(teacher, HttpStatus.OK);
        }
        else {
            result = new ResponseEntity<>(String.format("Lehrer/in mit der Id = %d nicht vorhanden", teacherId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value="{teacherId}/schoolclasses")
    public ResponseEntity<?> getByIdSchoolClass(@PathVariable Integer teacherId){
        logger.info(LogUtils.info(className, "getByIdSchoolClass",String.format("(%d)", teacherId)));
        ResponseEntity<?> result;
        Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

        if (optTeacher.isPresent()){
            Teacher teacher = optTeacher.get();
             result = new ResponseEntity<>(teacher.getSchoolClasses(), HttpStatus.OK);
        }
        else {
            result = new ResponseEntity<>(String.format("Lehrer/in mit der Id = %d hat keine Schulklasse", teacherId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

}

