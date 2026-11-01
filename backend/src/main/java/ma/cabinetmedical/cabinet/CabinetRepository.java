package ma.cabinetmedical.cabinet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CabinetRepository extends JpaRepository<Cabinet, UUID> {
    long countByActiveTrue();
    long countByActiveFalse();
    List<Cabinet> findByCreatedAtBetween(Instant from, Instant to);
}
