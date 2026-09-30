package smartclinic.project.backend.controllers;

import smartclinic.project.backend.models.Admin;
import smartclinic.project.backend.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private MainService service;



    
    /**
     * Обробляє запит на вхід адміністратора.
     * 
     * @param admin об'єкт Admin з тіла запиту (username та password)
     * @return ResponseEntity з токеном у разі успіху або повідомленням про помилку
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}