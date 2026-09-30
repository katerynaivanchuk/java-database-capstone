package smartclinic.project.backend.repositories; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Doctor; // Змініть імпорт моделі Doctor за потреби
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Знаходить лікаря за його email-адресою.
     */
    Doctor findByEmail(String email);

    /**
     * Пошук лікарів за частковим збігом імені.
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Фільтрація лікарів за частковим ім'ям та спеціальністю (без урахування регістру).
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(@Param("name") String name, 
                                                                       @Param("specialty") String specialty);

    /**
     * Знаходить лікарів за спеціальністю без урахування регістру.
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}