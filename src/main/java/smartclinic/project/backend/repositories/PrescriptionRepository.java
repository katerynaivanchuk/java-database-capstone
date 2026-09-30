package smartclinic.project.backend.repositories; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Prescription; // Змініть імпорт моделі Prescription за потреби
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Знаходить усі рецепти, пов'язані з конкретним записом на прийом (appointmentId).
     *
     * @param appointmentId ідентифікатор запису
     * @return список рецептів для вказаного запису
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}