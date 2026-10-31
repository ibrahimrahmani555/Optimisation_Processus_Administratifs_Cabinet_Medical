package ma.cabinetmedical.auth;

import ma.cabinetmedical.user.User;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        UUID cabinetId,
        String cabinetName
) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole().name(),
                user.getCabinet().getId(), user.getCabinet().getName()
        );
    }
}
