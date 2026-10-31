package ma.cabinetmedical.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "L'adresse e-mail est obligatoire")
        @Email(message = "L'adresse e-mail n'est pas valide")
        @Size(max = 190, message = "L'adresse e-mail est trop longue")
        String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(max = 200, message = "Le mot de passe est trop long")
        String password
) {
}
