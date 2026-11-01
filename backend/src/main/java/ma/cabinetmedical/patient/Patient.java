package ma.cabinetmedical.patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ma.cabinetmedical.cabinet.Cabinet;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false)
    private boolean archived;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Patient() {
    }

    public Patient(Cabinet cabinet, String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.cabinet = cabinet;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Cabinet getCabinet() { return cabinet; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isArchived() { return archived; }
    public Instant getCreatedAt() { return createdAt; }
}
