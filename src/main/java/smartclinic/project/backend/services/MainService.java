package smartclinic.project.backend.services;

import smartclinic.project.backend.dto.Login;
import smartclinic.project.backend.models.Admin;
import smartclinic.project.backend.models.Appointment;
import smartclinic.project.backend.models.Doctor;
import smartclinic.project.backend.models.Patient;
import smartclinic.project.backend.repositories.AdminRepository;
import smartclinic.project.backend.repositories.DoctorRepository;
import smartclinic.project.backend.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MainService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public MainService(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Валідує токен авторизації для конкретного користувача.
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }

    /**
     * Перевіряє облікові дані адміністратора та генерує токен.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(admin.getId(), admin.getUsername(), "ADMIN");
            response.put("token", token);
            response.put("message", "Admin login successful");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid admin credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Фільтрує лікарів за ім'ям, спеціальністю та доступним часом.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasSpecialty = specialty != null && !specialty.trim().isEmpty();
        boolean hasTime = time != null && !time.trim().isEmpty();

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (hasName) {
            return doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("doctors", doctorService.getDoctors());
            return response;
        }
    }

    /**
     * Перевіряє, чи вільний слот для запису до лікаря.
     * Повертає:
     *  1  - слот вільний і валідний
     *  0  - час зайнятий або недоступний
     * -1  - лікаря не знайдено
     */
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return -1;
        }

        Long doctorId = appointment.getDoctor().getId();
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (doctorOpt.isEmpty()) {
            return -1; // Лікаря не знайдено
        }

        LocalDate appointmentDate = appointment.getAppointmentDateOnly();
        LocalTime appointmentTime = appointment.getAppointmentTime();
        String formattedTime = appointmentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, appointmentDate);

        if (availableSlots.contains(formattedTime)) {
            return 1; // Час вільний
        }

        return 0; // Час недоступний/зайнятий
    }

    /**
     * Перевіряє, чи існує вже пацієнт із таким email або телефоном.
     * Повертає:
     * true  - пацієнта немає (можна реєструвати)
     * false - пацієнт вже існує
     */
    public boolean validatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findByEmailOrPhoneNumber(patient.getEmail(), patient.getPhoneNumber());
        return existingPatient == null;
    }

    /**
     * Валідує вхід пацієнта за email та паролем, повертає токен у разі успіху.
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login
    login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());

        if (patient != null && patient.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(patient.getId(), patient.getEmail(), "PATIENT");
            response.put("token", token);
            response.put("message", "Patient login successful");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Фільтрує прийоми пацієнта за станом (condition) та ім'ям лікаря.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        Long patientId = patient.getId();

        boolean hasCondition = condition != null && !condition.trim().isEmpty();
        boolean hasDoctorName = name != null && !name.trim().isEmpty();

        if (hasCondition && hasDoctorName) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        } else if (hasCondition) {
            return patientService.filterByCondition(condition, patientId);
        } else if (hasDoctorName) {
            return patientService.filterByDoctor(name, patientId);
        } else {
            return patientService.getPatientAppointment(patientId, token);
        }
    }
}