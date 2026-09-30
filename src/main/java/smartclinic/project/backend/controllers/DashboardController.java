package smartclinic.project.backend.controllers;

import com.exam.service.AuthService; // Змініть імпорт сервісу валідації токенів за потреби
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    private final AuthService authService;

    @Autowired
    public DashboardController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Відображає дашборд адміністратора при валідному токені.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        Map<String, Object> validationResult = authService.validateToken(token, "admin");

        // Якщо мапа результатів валідації порожня — токен валідний
        if (validationResult != null && validationResult.isEmpty()) {
            return "admin/adminDashboard";
        }

        // Якщо токен невалідний — редірект на сторінку авторизації
        return "redirect:http://localhost:8080";
    }

    /**
     * Відображає дашборд лікаря при валідному токені.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        Map<String, Object> validationResult = authService.validateToken(token, "doctor");

        // Якщо мапа порожня — токен валідний
        if (validationResult != null && validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        }

        // Якщо токен невалідний — редірект на сторінку авторизації
        return "redirect:http://localhost:8080";
    }
}