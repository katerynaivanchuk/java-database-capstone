package smartclinic.project.backend.controllers;

import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    private final MainService service;

    @Autowired
    public DashboardController(MainService service) {
        this.service = service;
    }

    /**
     * Відображає дашборд адміністратора при валідному токені.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");

        // Якщо статус успішний (2xx) — токен валідний
        if (validationResult.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard"; // назва вашого HTML-шаблону
        }

        // Якщо токен невалідний — редірект на головну сторінку
        return "redirect:/";
    }

    /**
     * Відображає дашборд лікаря при валідному токені.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "doctor");

        if (validationResult.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }

        return "redirect:/";
    }
}