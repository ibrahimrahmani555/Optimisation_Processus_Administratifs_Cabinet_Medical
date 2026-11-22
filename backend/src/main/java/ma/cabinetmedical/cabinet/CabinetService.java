package ma.cabinetmedical.cabinet;

import ma.cabinetmedical.audit.AuditAction;
import ma.cabinetmedical.audit.AuditLog;
import ma.cabinetmedical.audit.AuditLogRepository;
import ma.cabinetmedical.patient.PatientRepository;
import ma.cabinetmedical.user.Role;
import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

import static ma.cabinetmedical.cabinet.CabinetDtos.*;

@Service
public class CabinetService {
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Path UPLOAD_DIR = Paths.get("uploads", "cabinets");
    private final CabinetRepository cabinetRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AuditLogRepository auditLogRepository;

    public CabinetService(CabinetRepository cabinetRepository,
                          UserRepository userRepository,
                          PatientRepository patientRepository,
                          AuditLogRepository auditLogRepository) {
        this.cabinetRepository = cabinetRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public CabinetPage list(String search, CabinetStatus status, Pageable pageable) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        Page<Cabinet> page;
        if (normalizedSearch == null && status == CabinetStatus.ALL) {
            page = cabinetRepository.findAll(pageable);
        } else if (normalizedSearch == null && status == CabinetStatus.ACTIVE) {
            page = cabinetRepository.findByActive(true, pageable);
        } else if (normalizedSearch == null && status == CabinetStatus.INACTIVE) {
            page = cabinetRepository.findByActive(false, pageable);
        } else if (normalizedSearch != null && status == CabinetStatus.ALL) {
            page = cabinetRepository.findByNameContainingIgnoreCase(normalizedSearch, pageable);
        } else if (normalizedSearch != null && status == CabinetStatus.ACTIVE) {
            page = cabinetRepository.findByNameContainingIgnoreCaseAndActive(normalizedSearch, true, pageable);
        } else {
            page = cabinetRepository.findByNameContainingIgnoreCaseAndActive(normalizedSearch, false, pageable);
        }
        List<CabinetResponse> content = toResponses(page.getContent());
        CabinetSummary summary = new CabinetSummary(
                cabinetRepository.count(),
                cabinetRepository.countByActiveTrue(),
                cabinetRepository.countByActiveFalse()
        );
        return new CabinetPage(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                summary
        );
    }

    @Transactional(readOnly = true)
    public CabinetResponse get(UUID id) {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        return toResponses(List.of(cabinet)).get(0);
    }

    @Transactional
    public CabinetResponse create(CreateCabinetRequest request, JwtAuthenticationToken authentication) {
        String name = request.name().trim();
        if (cabinetRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateCabinetNameException();
        }
        Cabinet cabinet = cabinetRepository.save(new Cabinet(name));
        audit(authentication, AuditAction.CREATED, cabinet, "Création du cabinet " + cabinet.getName());
        return toResponses(List.of(cabinet)).get(0);
    }

    @Transactional
    public CabinetResponse update(UUID id, UpdateCabinetRequest request, JwtAuthenticationToken authentication) {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        String name = request.name().trim();
        if (cabinetRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateCabinetNameException();
        }
        cabinet.setName(name);
        cabinet = cabinetRepository.save(cabinet);
        audit(authentication, AuditAction.UPDATED, cabinet, "Modification du cabinet " + cabinet.getName());
        return toResponses(List.of(cabinet)).get(0);
    }

    @Transactional
    public CabinetResponse updateStatus(UUID id, UpdateCabinetStatusRequest request,
                                        JwtAuthenticationToken authentication) {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        cabinet.setActive(request.active());
        cabinet = cabinetRepository.save(cabinet);
        AuditAction action = request.active() ? AuditAction.ACTIVATED : AuditAction.DEACTIVATED;
        audit(authentication, action, cabinet,
                (request.active() ? "Activation" : "Désactivation") + " du cabinet " + cabinet.getName());
        return toResponses(List.of(cabinet)).get(0);
    }

    @Transactional
    public CabinetResponse uploadImage(UUID id, MultipartFile file,
                                       JwtAuthenticationToken authentication) {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez une image");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("L'image ne doit pas dépasser 2 Mo");
        }
        String contentType = file.getContentType();
        if (contentType == null || !IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Format d'image invalide. Formats acceptés : JPG, PNG ou WebP");
        }

        String extension = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String fileName = id + "-" + UUID.randomUUID() + extension;
        try {
            Files.createDirectories(UPLOAD_DIR);
            Files.copy(file.getInputStream(), UPLOAD_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'enregistrer l'image du cabinet");
        }

        deleteStoredImage(cabinet.getImageUrl());
        String imageUrl = "/api/uploads/cabinets/" + fileName;
        cabinet.setImageUrl(imageUrl);
        cabinet = cabinetRepository.save(cabinet);
        audit(authentication, AuditAction.UPDATED, cabinet, "Modification de l'image du cabinet " + cabinet.getName());
        return toResponses(List.of(cabinet)).get(0);
    }

    @Transactional
    public CabinetResponse deleteImage(UUID id, JwtAuthenticationToken authentication) {
        Cabinet cabinet = cabinetRepository.findById(id).orElseThrow(CabinetNotFoundException::new);
        deleteStoredImage(cabinet.getImageUrl());
        cabinet.setImageUrl(null);
        cabinet = cabinetRepository.save(cabinet);
        audit(authentication, AuditAction.UPDATED, cabinet, "Suppression de l'image du cabinet " + cabinet.getName());
        return toResponses(List.of(cabinet)).get(0);
    }

    private void deleteStoredImage(String imageUrl) {
        if (imageUrl == null) {
            return;
        }
        String prefix = "/api/uploads/cabinets/";
        if (!imageUrl.startsWith(prefix)) {
            return;
        }
        try {
            Files.deleteIfExists(UPLOAD_DIR.resolve(imageUrl.substring(prefix.length())));
        } catch (IOException ignored) {
        }
    }

    private UUID parseId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Identifiant UUID invalide");
        }
    }

    public UUID parseCabinetId(String id) {
        return parseId(id);
    }

    private List<CabinetResponse> toResponses(List<Cabinet> cabinets) {
        if (cabinets.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = cabinets.stream().map(Cabinet::getId).toList();
        Map<UUID, Long> doctors = new HashMap<>();
        Map<UUID, Long> secretaries = new HashMap<>();
        for (Object[] row : userRepository.countByCabinetAndRole(ids)) {
            UUID cabinetId = (UUID) row[0];
            Role role = (Role) row[1];
            long count = ((Number) row[2]).longValue();
            if (role == Role.MEDECIN) {
                doctors.put(cabinetId, count);
            } else if (role == Role.SECRETAIRE) {
                secretaries.put(cabinetId, count);
            }
        }
        Map<UUID, Long> patients = new HashMap<>();
        for (Object[] row : patientRepository.countByCabinets(ids)) {
            patients.put((UUID) row[0], ((Number) row[1]).longValue());
        }
        return cabinets.stream()
                .map(cabinet -> new CabinetResponse(
                        cabinet.getId(),
                        cabinet.getName(),
                        cabinet.isActive(),
                        cabinet.getCreatedAt(),
                        cabinet.getImageUrl(),
                        doctors.getOrDefault(cabinet.getId(), 0L),
                        secretaries.getOrDefault(cabinet.getId(), 0L),
                        patients.getOrDefault(cabinet.getId(), 0L)
                ))
                .toList();
    }

    private void audit(JwtAuthenticationToken authentication, AuditAction action,
                       Cabinet cabinet, String description) {
        UUID actorId = UUID.fromString(authentication.getToken().getSubject());
        User actor = userRepository.findWithCabinetById(actorId).orElse(null);
        String actorName = actor == null
                ? null
                : actor.getFirstName() + " " + actor.getLastName();
        auditLogRepository.save(new AuditLog(actorId, actorName, action, "CABINET", description));
    }
}
