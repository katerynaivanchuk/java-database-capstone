package smartclinic.project.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AppointmentType type;

    @NotNull
    @Column(nullable = false)
    private int status;

    @NotNull
    @Column(name = "appointment_date", nullable = false)
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentDate;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Transient
    public LocalDateTime getEndTime() {
        return appointmentDate.plusHours(1);
    }

    @Transient
    public LocalDate getAppointmentDateOnly() {
        return appointmentDate.toLocalDate();
    }

    @Transient
    public LocalTime getAppointmentTime() {
        return appointmentDate.toLocalTime();
    }
}
