package ma.cabinetmedical.cabinet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CabinetDtos {
    private CabinetDtos() {
    }

    public record CreateCabinetRequest(
            @NotBlank(message = "Le nom du cabinet est obligatoire")
            @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
            String name
    ) {}

    public record UpdateCabinetRequest(
            @NotBlank(message = "Le nom du cabinet est obligatoire")
            @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
            String name
    ) {}

    public record UpdateCabinetStatusRequest(
            @NotNull(message = "Le statut est obligatoire")
            Boolean active
    ) {}

    public record CabinetResponse(
            UUID id,
            String name,
            boolean active,
            Instant createdAt,
            String imageUrl,
            long doctors,
            long secretaries,
            long patients
    ) {}

    public record CabinetSummary(
            long total,
            long active,
            long inactive
    ) {}

    public record CabinetPage(
            List<CabinetResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            CabinetSummary summary
    ) {}

    public enum CabinetStatus {
        ALL,
        ACTIVE,
        INACTIVE
    }
}
