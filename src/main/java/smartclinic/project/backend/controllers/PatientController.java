package smartclinic.project.backend.controllers;

import smartclinic.project.backend.dto.Login;
import smartclinic.project.backend.models.Patient;
import smartclinic.project.backend.services.PatientService;
import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final MainService service;

    @Autowired
    public PatientController(PatientService patientService, MainService service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * 1. Отримати деталі пацієнта за токеном.
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            return patientService.getPatientDetails(token);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching patient details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 2. Реєстрація нового пацієнта.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        // Перевірка, чи існує пацієнт із таким email або телефоном
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            response.put("message", "Patient with email id or phone no already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            int result = patientService.createPatient(patient);
            if (result == 1) {
                response.put("message", "Signup successful");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Internal server error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 3. Вхід пацієнта (Логін).
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    /**
     * 4. Отримати прийоми пацієнта за його ID та токеном.
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            return patientService.getPatientAppointment(id, token);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 5. Фільтрувати прийоми пацієнта за станом (condition) та ім'ям/описом (name).
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {
        // Перевіряємо валідність токена через service
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", tokenValidation.getBody() != null ? tokenValidation.getBody().get("message") : "Unauthorized");
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(errorResponse);
        }

        // Обробка заглушок шляхiв ("null" або "-")
        String searchCondition = "null".equalsIgnoreCase(condition) || "-".equals(condition) ? null : condition;
        String searchName = "null".equalsIgnoreCase(name) || "-".equals(name) ? null : name;

        return service.filterPatient(searchCondition, searchName, token);
    }
}