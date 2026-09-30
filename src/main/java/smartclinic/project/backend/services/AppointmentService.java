package smartclinic.project.backend.services; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Appointment;
import smartclinic.project.backend.repositories.AppointmentRepository;
import smartclinic.project.backend.repositories.DoctorRepository;
import smartclinic.project.backend.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Бронює новий запис на прийом.
     *
     * @param appointment об'єкт запису для створення
     * @return 1 у разі успіху, 0 у разі помилки
     */
    public int bookAppointment(Appointment appointment) {
        try {
            if (appointment == null) {
                return 0;
            }
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Оновлює існуючий запис на прийом з попередньою перевіркою існування та валідацією.
     *
     * @param appointment об'єкт запису з оновленими даними
     * @return ResponseEntity з повідомленням про результат операції
     */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        if (appointment == null || appointment.getId() == null) {
            response.put("message", "Invalid appointment data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(appointment.getId());
        if (existingAppointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Збереження оновленого запису
        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Скасовує (видаляє) запис на прийом після перевірки прав доступу за токеном.
     *
     * @param id    ID запису для скасування
     * @param token токен авторизації
     * @return ResponseEntity з повідомленням про результат
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Валідація токена та перевірка, що скасування виконує саме той пацієнт, який забронював прийом
        if (token != null && tokenService != null) {
            Long userIdFromToken = tokenService.getUserIdFromToken(token);
            if (userIdFromToken != null && !userIdFromToken.equals(appointment.getPatientId())) {
                response.put("message", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Отримує список записів для конкретного лікаря на вказану дату з можливістю фільтрації за ім'ям пацієнта.
     *
     * @param pname ім'я пацієнта для фільтрації (може бути порожнім)
     * @param date  дата прийому
     * @param token токен авторизації лікаря
     * @return Map зі списком записів
     */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        Long doctorId = tokenService != null ? tokenService.getUserIdFromToken(token) : null;
        if (doctorId == null) {
            response.put("appointments", List.of());
            return response;
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;

        if (pname != null && !pname.trim().isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, pname.trim(), startOfDay, endOfDay
            );
        } else {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId, startOfDay, endOfDay
            );
        }

        response.put("appointments", appointments);
        return response;
    }
}