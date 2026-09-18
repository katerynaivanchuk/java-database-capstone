package smartclinic.project.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_DOCTOR;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotBlank
    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 1, max = 50)
    private String specialty;

    @NotBlank
    @Size(min = 5, max = 15)
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @ElementCollection
    @CollectionTable(
            name = "doctor_availabilities",
            joinColumns = @JoinColumn(name = "doctor_id")
    )
    private List<DoctorAvailability> availableTimes = new ArrayList<>();

    @NotBlank
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;
}