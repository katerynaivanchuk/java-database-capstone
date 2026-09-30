package smartclinic.project.backend.services;

import smartclinic.project.backend.dto.Login; 
import smartclinic.project.backend.models.Appointment;
import smartclinic.project.backend.models.Doctor;
import smartclinic.project.backend.repositories.AppointmentRepository;
import smartclinic.project.backend.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    private static final List<String> ALL_SLOTS = List.of(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
    );

    /**
     * Отримує список вільних слотів для лікаря на конкретну дату.
     */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay);

        List<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        List<String> availableSlots = new ArrayList<>(ALL_SLOTS);
        availableSlots.removeAll(bookedSlots);

        return availableSlots;
    }

    /**
     * Зберігає нового лікаря в базу даних, якщо лікар з таким email ще не існує.
     */
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // Лікар вже існує
            }
            doctorRepository.save(doctor);
            return 1; // Успішно збережено
        } catch (Exception e) {
            return 0; // Внутрішня помилка
        }
    }

    /**
     * Оновлює дані існуючого лікаря.
     */
    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1; // Лікаря не знайдено
            }
            doctorRepository.save(doctor);
            return 1; // Успішно оновлено
        } catch (Exception e) {
            return 0; // Внутрішня помилка
        }
    }

    /**
     * Повертає список усіх лікарів.
     */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Видаляє лікаря за ID та всі пов'язані з ним записи на прийом.
     */
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1; // Лікаря не знайдено
            }
            appointmentRepository.deleteAllByDoctor_Id(id);
            doctorRepository.deleteById(id);
            return 1; // Успішно видалено
        } catch (Exception e) {
            return 0; // Внутрішня помилка
        }
    }

    /**
     * Валідує облікові дані лікаря та генерує токен авторизації.
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(doctor.getId(), doctor.getEmail(), "DOCTOR");
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Знаходить лікарів за ім'ям (частковий збіг).
     */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * Фільтрує лікарів за ім'ям, спеціальністю та часом (AM/PM).
     */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        response.put("doctors", filteredDoctors);
        return response;
    }

    /**
     * Фільтрує лікарів за ім'ям та часом (AM/PM).
     */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        response.put("doctors", filteredDoctors);
        return response;
    }

    /**
     * Фільтрує лікарів за ім'ям та спеціальністю.
     */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * Фільтрує лікарів за спеціальністю та часом (AM/PM).
     */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        response.put("doctors", filteredDoctors);
        return response;
    }

    /**
     * Фільтрує лікарів за спеціальністю.
     */
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * Фільтрує всіх лікарів за часом прийому (AM/PM).
     */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Doctor> filteredDoctors = filterDoctorByTime(allDoctors, amOrPm);
        response.put("doctors", filteredDoctors);
        return response;
    }

    /**
     * Приватний допоміжний метод для фільтрації списку лікарів за доступними годинами (AM / PM).
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.trim().isEmpty()) {
            return doctors;
        }

        return doctors.stream().filter(doctor -> {
            if (doctor.getAvailableTimes() != null) {
                return doctor.getAvailableTimes().stream().anyMatch(availability -> {
                    try {
                        int startHour = availability.getStartTime().getHour();
                        int endHour = availability.getEndTime().getHour();

                        if ("AM".equalsIgnoreCase(amOrPm)) {
                            return startHour < 12;
                        } else if ("PM".equalsIgnoreCase(amOrPm)) {
                            return endHour >= 12;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                    return false;
                });
            }
            return false;
        }).collect(Collectors.toList());
    }
}