package ma.cabinetmedical.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ma.cabinetmedical.cabinet.Cabinet;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 190)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(Cabinet cabinet, String firstName, String lastName, String email, String passwordHash, Role role) {
        this.id = UUID.randomUUID();
        this.cabinet = cabinet;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = normalizeEmail(email);
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public UUID getId() { return id; }
    public Cabinet getCabinet() { return cabinet; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setActive(boolean active) { this.active = active; this.updatedAt = Instant.now(); }
}
