package smartclinic.project.backend.controllers;

import smartclinic.project.backend.dto.Login;
import smartclinic.project.backend.models.Doctor;
import smartclinic.project.backend.services.DoctorService;
import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final MainService service;

    @Autowired
    public DoctorController(DoctorService doctorService, MainService service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * 1. Отримати доступність лікаря на конкретну дату.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        try {
            List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
            Map<String, Object> response = new HashMap<>();
            response.put("availability", availability);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error fetching doctor availability: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 2. Отримати список усіх лікарів.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Додати нового лікаря (доступно адміністратору).
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {
        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            response.put("message", "Doctor added to db");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 4. Логін лікаря.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * 5. Оновити деталі лікаря.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {
        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            response.put("message", "Doctor updated");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 6. Видалити лікаря за ID.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor not found with id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 7. Фільтрувати лікарів за ім'ям, часом та спеціальністю.
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {
        // Якщо передані значення-заглушки (наприклад, "null" або "-" у шляху), перетворюємо їх на null
        String searchName = "null".equalsIgnoreCase(name) || "-".equals(name) ? null : name;
        String searchTime = "null".equalsIgnoreCase(time) || "-".equals(time) ? null : time;
        String searchSpeciality = "null".equalsIgnoreCase(speciality) || "-".equals(speciality) ? null : speciality;

        Map<String, Object> filteredDoctors = service.filterDoctor(searchName, searchSpeciality, searchTime);
        return ResponseEntity.ok(filteredDoctors);
    }
}