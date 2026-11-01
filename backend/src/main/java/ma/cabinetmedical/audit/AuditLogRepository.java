package ma.cabinetmedical.audit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @Query("select log from AuditLog log order by log.occurredAt desc")
    List<AuditLog> findRecent(PageRequest pageRequest);
}
