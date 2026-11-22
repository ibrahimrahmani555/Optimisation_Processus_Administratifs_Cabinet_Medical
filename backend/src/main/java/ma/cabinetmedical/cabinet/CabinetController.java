package ma.cabinetmedical.cabinet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

import static ma.cabinetmedical.cabinet.CabinetDtos.*;

@RestController
@RequestMapping("/api/admin/cabinets")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class CabinetController {
    private static final Set<String> SORT_FIELDS = Set.of("name", "createdAt", "active");

    private final CabinetService cabinetService;

    public CabinetController(CabinetService cabinetService) {
        this.cabinetService = cabinetService;
    }

    @GetMapping
    CabinetPage list(@RequestParam(defaultValue = "0") @Min(0) int page,
                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
                     @RequestParam(defaultValue = "createdAt,desc") String sort,
                     @RequestParam(required = false) String search,
                     @RequestParam(defaultValue = "ALL") CabinetStatus status) {
        return cabinetService.list(search, status, pageable(page, size, sort));
    }

    @GetMapping("/{id}")
    CabinetResponse get(@PathVariable String id) {
        return cabinetService.get(cabinetService.parseCabinetId(id));
    }

    @PostMapping
    CabinetResponse create(@Valid @RequestBody CreateCabinetRequest request,
                           JwtAuthenticationToken authentication) {
        return cabinetService.create(request, authentication);
    }

    @PutMapping("/{id}")
    CabinetResponse update(@PathVariable String id,
                           @Valid @RequestBody UpdateCabinetRequest request,
                           JwtAuthenticationToken authentication) {
        UUID cabinetId = cabinetService.parseCabinetId(id);
        return cabinetService.update(cabinetId, request, authentication);
    }

    @PatchMapping("/{id}/status")
    CabinetResponse updateStatus(@PathVariable String id,
                                 @Valid @RequestBody UpdateCabinetStatusRequest request,
                                 JwtAuthenticationToken authentication) {
        UUID cabinetId = cabinetService.parseCabinetId(id);
        return cabinetService.updateStatus(cabinetId, request, authentication);
    }

    @PostMapping("/{id}/image")
    CabinetResponse uploadImage(@PathVariable String id,
                                @RequestParam("file") MultipartFile file,
                                JwtAuthenticationToken authentication) {
        UUID cabinetId = cabinetService.parseCabinetId(id);
        return cabinetService.uploadImage(cabinetId, file, authentication);
    }

    @DeleteMapping("/{id}/image")
    CabinetResponse deleteImage(@PathVariable String id,
                                JwtAuthenticationToken authentication) {
        UUID cabinetId = cabinetService.parseCabinetId(id);
        return cabinetService.deleteImage(cabinetId, authentication);
    }

    private Pageable pageable(int page, int size, String sort) {
        String[] parts = sort.split(",", 2);
        String field = parts[0];
        if (!SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException("Champ de tri invalide");
        }
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}
