package smartclinic.project.backend.repositories; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Patient; // Змініть імпорт моделі Patient за потреби
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Знаходить пацієнта за його email-адресою.
     *
     * @param email email пацієнта
     * @return об'єкт Patient або null, якщо пацієнта не знайдено
     */
    Patient findByEmail(String email);

    /**
     * Знаходить пацієнта за email-адресою або номером телефону.
     *
     * @param email email пацієнта
     * @param phone номер телефону пацієнта
     * @return об'єкт Patient або null, якщо збігів не знайдено
     */
    Patient findByEmailOrPhone(String email, String phone);
}