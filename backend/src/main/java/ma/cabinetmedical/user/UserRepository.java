package ma.cabinetmedical.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "cabinet")
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "cabinet")
    Optional<User> findWithCabinetById(UUID id);

    long countByRole(Role role);
    List<User> findByCreatedAtBetween(Instant from, Instant to);

    @Query("""
            select u.cabinet.id, u.role, count(u.id)
            from User u
            where u.cabinet.id in :cabinetIds
            group by u.cabinet.id, u.role
            """)
    List<Object[]> countByCabinetAndRole(@Param("cabinetIds") List<UUID> cabinetIds);
}
