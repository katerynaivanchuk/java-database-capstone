package smartclinic.project.backend.repositories; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Appointment; // Змініть імпорт моделі Appointment за потреби
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Повертає записи лікаря у заданій часовій рамці з підвантаженням зв'язаних сутностей.
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availabilities WHERE a.doctorId = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(@Param("doctorId") Long doctorId, 
                                                               @Param("start") LocalDateTime start, 
                                                               @Param("end") LocalDateTime end);

    /**
     * Фільтрує записи за ID лікаря, частковим ім'ям пацієнта (без урахування регістру) та часовим інтервалом.
     */
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d WHERE a.doctorId = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(@Param("doctorId") Long doctorId, 
                                                                                                  @Param("patientName") String patientName, 
                                                                                                  @Param("start") LocalDateTime start, 
                                                                                                  @Param("end") LocalDateTime end);

    /**
     * Видаляє всі записи, пов'язані з конкретним лікарем.
     */
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    /**
     * Знаходить усі записи для конкретного пацієнта.
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Повертає записи пацієнта за статусом, відсортовані за часом у порядку зростання.
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    /**
     * Пошук записів за частковим ім'ям лікаря та ID пацієнта.
     */
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patientId = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(@Param("doctorName") String doctorName, 
                                                     @Param("patientId") Long patientId);

    /**
     * Фільтрація записів за ім'ям лікаря, ID пацієнта та статусом запису.
     */
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patientId = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(@Param("doctorName") String doctorName, 
                                                              @Param("patientId") Long patientId, 
                                                              @Param("status") int status);
}