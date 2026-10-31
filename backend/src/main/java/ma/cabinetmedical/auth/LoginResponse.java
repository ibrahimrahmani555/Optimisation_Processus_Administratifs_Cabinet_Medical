package ma.cabinetmedical.auth;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, AuthUserResponse user) {
}
