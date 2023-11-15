package at.htlklu.spring.repository;

import at.htlklu.spring.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer>
{
    // to do
    // Spezifizieren einer Methode, die die Liste aller Teacher sortiert liefert
    // VT: Sortierung erfolgt in der Datenbank
    List<Teacher> findByOrderBySurnameAscFirstnameAsc();
}
