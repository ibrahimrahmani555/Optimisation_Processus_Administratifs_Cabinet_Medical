package ma.cabinetmedical.auth;

import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    private static final String DUMMY_HASH = "$2a$12$C6UzMDM.H6dfI/f/IKcEe.og1yZcQlnmK7X8WQZgV4zS3xQ.0wXGy";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Duration tokenLifetime;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
                       @Value("${app.security.access-token-minutes:30}") long accessTokenMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.tokenLifetime = Duration.ofMinutes(accessTokenMinutes);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(User.normalizeEmail(request.email())).orElse(null);
        String hash = user == null ? DUMMY_HASH : user.getPasswordHash();
        boolean passwordMatches = passwordEncoder.matches(request.password(), hash);
        if (user == null || !passwordMatches || !user.isActive() || !user.getCabinet().isActive()) {
            throw new InvalidCredentialsException();
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenLifetime);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cabinet-medical-api")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("cabinetId", user.getCabinet().getId().toString())
                .claim("email", user.getEmail())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", tokenLifetime.toSeconds(), AuthUserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public AuthUserResponse currentUser(String subject) {
        UUID id;
        try {
            id = UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findWithCabinetById(id)
                .filter(User::isActive)
                .filter(found -> found.getCabinet().isActive())
                .orElseThrow(InvalidCredentialsException::new);
        return AuthUserResponse.from(user);
    }
}
