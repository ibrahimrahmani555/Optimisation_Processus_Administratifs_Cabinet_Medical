package ma.cabinetmedical.cabinet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CabinetRepository extends JpaRepository<Cabinet, UUID> {
}
