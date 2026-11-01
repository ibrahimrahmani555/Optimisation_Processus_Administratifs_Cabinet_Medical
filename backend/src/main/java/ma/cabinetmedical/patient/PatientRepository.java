package ma.cabinetmedical.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    long countByArchivedFalse();
    List<Patient> findByCreatedAtBetween(Instant from, Instant to);
}
