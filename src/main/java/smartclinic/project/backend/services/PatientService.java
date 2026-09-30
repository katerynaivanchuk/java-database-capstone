package smartclinic.project.backend.services;

import smartclinic.project.backend.dto.AppointmentDTO;
import smartclinic.project.backend.models.Appointment;
import smartclinic.project.backend.models.Patient;
import smartclinic.project.backend.repositories.AppointmentRepository;
import smartclinic.project.backend.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * 1. Зберігає нового пацієнта в базу даних.
     */
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 2. Отримує список прийомів пацієнта з перевіркою токена авторизації.
     */
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Фільтрує прийоми пацієнта за станом (past / future).
     */
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;

        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(id, status);
        List<AppointmentDTO> dtos = appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    /**
     * 4. Фільтрує прийоми пацієнта за ім'ям лікаря.
     */
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, name);
        List<AppointmentDTO> dtos = appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. Фільтрує прийоми пацієнта за ім'ям лікаря та станом (past / future).
     */
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;

        List<Appointment> appointments = appointmentRepository
                .findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, name, status);
        List<AppointmentDTO> dtos = appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.put("appointments", dtos);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. Отримує деталі пацієнта за токеном.
     */
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }

    /**
     * Допоміжний метод для конвертації Entity Appointment у AppointmentDTO.
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorName(appointment.getDoctor() != null ? appointment.getDoctor().getName() : null);
        dto.setDoctorSpecialty(appointment.getDoctor() != null ? appointment.getDoctor().getSpecialty() : null);
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        return dto;
    }
}