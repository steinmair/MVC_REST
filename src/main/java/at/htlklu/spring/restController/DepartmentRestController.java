package at.htlklu.spring.restController;

import at.htlklu.spring.api.ErrorsUtils;
import at.htlklu.spring.api.HateoasUtils;
import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Department;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.model.Student;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.DepartmentRepository;
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
@RequestMapping("departments")
public class DepartmentRestController extends RepresentationModel {
    private static final Logger logger = LogManager.getLogger(DepartmentRestController.class);
    private static final String className = "DepartmentRestController";

    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    //http://localhost:8082/teachers/1
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        logger.info(LogUtils.info(className, "getAllDepartments", "Get all departments"));
        List<Department> departments = departmentRepository.findAll();
        if (!departments.isEmpty()) {

            return new ResponseEntity<>(departments, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(value = "{departmentId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer departmentId) {
        logger.info(LogUtils.info(className, "getByPV", String.format("(%d)", departmentId)));
        ResponseEntity<?> result;
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);


        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            addLinks(department);
            result = new ResponseEntity<>(department, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Department mit der Id = %d nicht vorhanden", departmentId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{departmentId}/schoolClasses")
    public ResponseEntity<?> getByIdSchoolClasses(@PathVariable Integer departmentId) {
        logger.info(LogUtils.info(className, "getByIdSchoolClasses", String.format("(%d)", departmentId)));
        ResponseEntity<?> result;
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);

        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            DepartmentRestController.addLinks(department);
            Set<SchoolClass> schoolClasses =  department.getSchoolClasses();
            schoolClasses.forEach(schoolClass -> SchoolClassRestController.addLinks(schoolClass));
            result = new ResponseEntity<>(schoolClasses, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Department mit der Id = %d hat keine SchoolClasses", departmentId),
                    HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping(value = "{departmentId}/teacher")
    public ResponseEntity<?> getByIdTeachers(@PathVariable Integer departmentId){
        logger.info(LogUtils.info(className, "getByIdTeacher", String.format("(%d)", departmentId)));
        ResponseEntity<?> result;
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);

        if (optionalDepartment.isPresent()){
            Department department = optionalDepartment.get();
            DepartmentRestController.addLinks(department);
            Teacher teacher =  department.getTeacher();
            TeacherRestController.addLinks(teacher);
            result = new ResponseEntity<>(teacher, HttpStatus.OK);
        }else {
            result = new ResponseEntity<>(String.format("Department mit der Id= %d hat keinen Teacher!", departmentId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //region Put and Push
// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Department department,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "add", String.format("(%s)", department)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                departmentRepository.save(department);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(department, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Department department,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(className, "update", String.format("(%s)", department)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                departmentRepository.save(department);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<>(department, HttpStatus.OK);

        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

//endregion

    @DeleteMapping(value = "{departmentId}")
    public ResponseEntity<?> deletePV2(@PathVariable Integer departmentId) {
        logger.info(LogUtils.info(className, "deletePV2", String.format("(%d)", departmentId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Department department = null;


        if (!error) {
            Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
            if (optionalDepartment.isPresent()) {
                department = optionalDepartment.get();
            } else {
                error = true;
                errorMessage = "Department not found";
            }
        }

        if (!error) {
            try {
                departmentRepository.delete(department);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<>(department, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    public static void addLinks(Department department){
        if (HateoasUtils.enableHateoas){
            department.add(WebMvcLinkBuilder.linkTo(methodOn(DepartmentRestController.class)
                            .getByIdPV(department.getDepartmentId()))
                            .withSelfRel());
            department.add(WebMvcLinkBuilder.linkTo(methodOn(DepartmentRestController.class)
                            .getByIdSchoolClasses(department.getDepartmentId()))
                            .withRel("schoolClasses"));
            department.add(WebMvcLinkBuilder.linkTo(methodOn(DepartmentRestController.class)
                    .getByIdTeachers(department.getDepartmentId())).withRel("teacher"));
        }
    }
}




