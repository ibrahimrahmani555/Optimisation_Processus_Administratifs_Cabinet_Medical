package ma.cabinetmedical.cabinet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cabinets")
public class Cabinet {
    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Cabinet() {
    }

    public Cabinet(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setActive(boolean active) { this.active = active; }
}
