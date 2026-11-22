package ma.cabinetmedical.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    long countByArchivedFalse();
    List<Patient> findByCreatedAtBetween(Instant from, Instant to);

    @Query("""
            select p.cabinet.id, count(p.id)
            from Patient p
            where p.cabinet.id in :cabinetIds
            group by p.cabinet.id
            """)
    List<Object[]> countByCabinets(@Param("cabinetIds") List<UUID> cabinetIds);
}
