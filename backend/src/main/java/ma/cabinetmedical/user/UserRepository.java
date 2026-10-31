package ma.cabinetmedical.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "cabinet")
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "cabinet")
    Optional<User> findWithCabinetById(UUID id);
}
