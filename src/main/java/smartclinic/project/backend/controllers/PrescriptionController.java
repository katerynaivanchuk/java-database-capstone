package smartclinic.project.backend.controllers;

import smartclinic.project.backend.models.Prescription;
import smartclinic.project.backend.services.PrescriptionService;
import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final MainService service;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, MainService service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    /**
     * 1. Збереження рецепта (доступно лише лікарям).
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {
        // Валідація токена для ролі "doctor"
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        // Збереження рецепта через сервіс
        return prescriptionService.savePrescription(prescription);
    }

    /**
     * 2. Отримання рецепта за ID прийому (доступно лише лікарям).
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        // Валідація токена для ролі "doctor"
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        // Отримання рецепта за ID прийому
        return prescriptionService.getPrescription(appointmentId);
    }
}