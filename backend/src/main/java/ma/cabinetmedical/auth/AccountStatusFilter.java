package ma.cabinetmedical.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.cabinetmedical.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountStatusFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AccountStatusFilter(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken authentication) {
            boolean accountEnabled;
            try {
                UUID userId = UUID.fromString(authentication.getToken().getSubject());
                accountEnabled = userRepository.findWithCabinetById(userId)
                        .map(user -> user.isActive() && user.getCabinet().isActive())
                        .orElse(false);
            } catch (IllegalArgumentException exception) {
                accountEnabled = false;
            }

            if (!accountEnabled) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(), Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Unauthorized",
                        "message", "Compte ou cabinet inactif"
                ));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
