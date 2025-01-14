package at.htlklu.spring.repository;

import at.htlklu.spring.model.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer>
{
    List<Department> findAll();


    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
             attributePaths = {"teacher",
                                "schoolClasses",
                                "schoolClasses.students",
                                "schoolClasses.events"})

    List<Department> findByOrderByNameAsc();


}
