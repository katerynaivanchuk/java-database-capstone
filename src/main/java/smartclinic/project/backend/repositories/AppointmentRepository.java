package smartclinic.project.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smartclinic.project.backend.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d WHERE d.id = :doctorId AND LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findByDoctor_IdAndPatient_NameContainingIgnoreCaseAndAppointmentDateBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d WHERE d.id = :doctorId AND a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findByDoctor_IdAndAppointmentDateBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    void deleteAllByDoctor_Id(Long doctorId);

    // Для PatientService (методи, яких не вистачало)
    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentDateAsc(Long patientId, int status);

    @Query("SELECT a FROM Appointment a JOIN a.doctor d WHERE d.name LIKE CONCAT('%', :doctorName, '%') AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(@Param("doctorName") String doctorName, @Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a JOIN a.doctor d WHERE d.name LIKE CONCAT('%', :doctorName, '%') AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(@Param("doctorName") String doctorName, @Param("patientId") Long patientId, @Param("status") int status);
}