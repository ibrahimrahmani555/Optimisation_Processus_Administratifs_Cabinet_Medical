package ma.cabinetmedical.cabinet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CabinetRepository extends JpaRepository<Cabinet, UUID> {
    long countByActiveTrue();
    long countByActiveFalse();
    List<Cabinet> findByCreatedAtBetween(Instant from, Instant to);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    Page<Cabinet> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Cabinet> findByActive(boolean active, Pageable pageable);
    Page<Cabinet> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);
}
