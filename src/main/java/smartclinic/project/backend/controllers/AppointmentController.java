package smartclinic.project.backend.controllers;

import smartclinic.project.backend.models.Appointment;
import smartclinic.project.backend.services.AppointmentService;
import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final MainService service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, MainService service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Отримує список прийомів за датою та ім'ям пацієнта (доступно тільки лікарям).
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        // Валідація токена для ролі "doctor"
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            LocalDate parsedDate = LocalDate.parse(date);
            // Обгортаємо результат сервісу у ResponseEntity.ok()
            Map<String, Object> result = appointmentService.getAppointment(patientName, parsedDate, token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error retrieving appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Бронює новий прийом (доступно пацієнтам).
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {
        Map<String, String> response = new HashMap<>();

        // Валідація токена для ролі "patient"
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        // Валідація доступності часу прийому
        int appointmentValidationResult = service.validateAppointment(appointment);
        if (appointmentValidationResult == -1) {
            response.put("message", "Doctor does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (appointmentValidationResult == 0) {
            response.put("message", "Appointment time is unavailable or busy");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Оскільки bookAppointment повертає int (результат), формуємо відповідь самостійно
            int bookingResult = appointmentService.bookAppointment(appointment);
            response.put("message", "Appointment booked successfully");
            response.put("status", String.valueOf(bookingResult));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error booking appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Оновлює існуючий прийом (доступно пацієнтам).
     */
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            return appointmentService.updateAppointment(appointment);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Скасовує прийом за його ID (доступно пацієнтам).
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            return appointmentService.cancelAppointment(id, token);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error canceling appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}