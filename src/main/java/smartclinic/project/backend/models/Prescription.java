package smartclinic.project.backend.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    @Size(min = 3, max = 100)
    private String patientName;

    @NotNull
    private Long doctorId;

    @NotNull
    private String doctorName;

    @NotNull
    @Size(min = 3, max = 100)
    private String medication;

    @NotNull
    @Size(min = 3, max = 20)
    private String dosage;

    @Size(max = 200)
    private String doctorNotes;

    private Instant createdAt = Instant.now();

    private Instant validUntil;


}
