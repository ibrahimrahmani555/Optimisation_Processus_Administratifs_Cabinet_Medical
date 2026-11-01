package ma.cabinetmedical.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    private UUID id;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_name", length = 170)
    private String actorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected AuditLog() {
    }

    public AuditLog(UUID actorId, String actorName, AuditAction action, String entityType, String description) {
        this.id = UUID.randomUUID();
        this.actorId = actorId;
        this.actorName = actorName;
        this.action = action;
        this.entityType = entityType;
        this.description = description;
        this.occurredAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getActorId() { return actorId; }
    public String getActorName() { return actorName; }
    public AuditAction getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getDescription() { return description; }
    public Instant getOccurredAt() { return occurredAt; }
}
