package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.HateoasUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Student;
import at.htlklu.spring.repository.AbsenceRepository;
import at.htlklu.spring.repository.AddressRepository;
import at.htlklu.spring.repository.StudentRepository;
import at.htlklu.spring.repository.StudentSubjectRepository;
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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("events")
public class StudentRestController extends RepresentationModel {
    private static final Logger logger = LogManager.getLogger(StudentRestController.class);
    private static final String className = "DepartmentRestController";


    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AbsenceRepository absenceRepository;
    @Autowired
    StudentSubjectRepository studentSubjectRepository;
    @Autowired
    AddressRepository addressRepository;

    //http://localhost:8082/teachers/1
    @GetMapping(value = "{studentId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer studentId) {
        logger.info(LogUtils.info(className, "getByPV", String.format("(%d)", studentId)));
        ResponseEntity<?> result;
        Optional<Student> optionalStudent = studentRepository.findById(studentId);


        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            addLinks(student);
            result = new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Student mit der Id = %d nicht vorhanden", studentId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{studentId}/addresses")
    public ResponseEntity<?> getByIdAdress(@PathVariable Integer studentId) {
        logger.info(LogUtils.info(className, "getByIdAdresses", String.format("(%d)", studentId)));
        ResponseEntity<?> result;
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            result = new ResponseEntity<>(student.getAddresses(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Student mit der Id = %d hat keine Addresse", studentId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{studentId}/absences")
    public ResponseEntity<?> getByIdAbsences(@PathVariable Integer studentId){
        logger.info(LogUtils.info(className, "getByIdAbsences", String.format("(%d)", studentId)));
        ResponseEntity<?> result;
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            result = new ResponseEntity<>(student.getAbsences(), HttpStatus.OK);
        }else {
            result = new ResponseEntity<>(String.format("Student mit der Id= %d hat keine Absences!", studentId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //region Put and Push
// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Student student,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "add", String.format("(%s)", student)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                studentRepository.save(student);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(student, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Student student,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "update", String.format("(%s)", student)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                studentRepository.save(student);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(student, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

//endregion

    @DeleteMapping(value = "{studentId}")
    public ResponseEntity<?> deletePV2(@PathVariable Integer studentId) {
        logger.info(LogUtils.info(className, "deletePV2", String.format("(%d)", studentId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Student student = null;


        if (!error) {
            Optional<Student> optionalStudent = studentRepository.findById(studentId);
            if (optionalStudent.isPresent()) {
                student = optionalStudent.get();
            } else {
                error = true;
                errorMessage = "Student not found";
            }
        }

        if (!error) {
            try {
                studentRepository.delete(student);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static void addLinks(Student student){
        if (HateoasUtils.enableHateoas){
            student.add(WebMvcLinkBuilder.linkTo(methodOn(StudentRestController.class)
                            .getByIdPV(student.getStudentId()))
                            .withSelfRel());
            student.add(WebMvcLinkBuilder.linkTo(methodOn(StudentRestController.class)
                            .getByIdAdress(student.getStudentId()))
                            .withRel("addresses"));
            student.add(WebMvcLinkBuilder.linkTo(methodOn(StudentRestController.class)
                    .getByIdAbsences(student.getStudentId())).withRel("absences"));
        }
    }
}




