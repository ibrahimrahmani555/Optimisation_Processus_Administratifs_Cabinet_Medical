package ma.cabinetmedical.cabinet;

import ma.cabinetmedical.audit.AuditAction;
import ma.cabinetmedical.audit.AuditLogRepository;
import ma.cabinetmedical.patient.Patient;
import ma.cabinetmedical.patient.PatientRepository;
import ma.cabinetmedical.user.Role;
import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CabinetControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired CabinetRepository cabinetRepository;
    @Autowired UserRepository userRepository;
    @Autowired PatientRepository patientRepository;
    @Autowired AuditLogRepository auditLogRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Cabinet activeCabinet;
    private Cabinet inactiveCabinet;
    private User admin;
    private User doctor;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        patientRepository.deleteAll();
        userRepository.deleteAll();
        cabinetRepository.deleteAll();

        activeCabinet = cabinetRepository.save(new Cabinet("Cabinet Alpha"));
        inactiveCabinet = cabinetRepository.save(new Cabinet("Cabinet Beta"));
        inactiveCabinet.setActive(false);
        inactiveCabinet = cabinetRepository.save(inactiveCabinet);

        admin = userRepository.save(new User(activeCabinet, "Admin", "Global",
                "admin-cabinets@test.ma", passwordEncoder.encode("Password123!"), Role.ADMIN));
        doctor = userRepository.save(new User(activeCabinet, "Docteur", "Test",
                "doctor-cabinets@test.ma", passwordEncoder.encode("Password123!"), Role.MEDECIN));
        userRepository.save(new User(activeCabinet, "Secretaire", "Test",
                "secretary-cabinets@test.ma", passwordEncoder.encode("Password123!"), Role.SECRETAIRE));
        patientRepository.save(new Patient(activeCabinet, "Patient", "Test"));
    }

    @Test
    void listsFiltersAndPaginatesCabinetData() throws Exception {
        mockMvc.perform(get("/api/admin/cabinets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "alpha")
                        .param("status", "ACTIVE")
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Cabinet Alpha"))
                .andExpect(jsonPath("$.content[0].doctors").value(1))
                .andExpect(jsonPath("$.content[0].secretaries").value(1))
                .andExpect(jsonPath("$.content[0].patients").value(1))
                .andExpect(jsonPath("$.summary.total").value(2));
    }

    @Test
    void createsUpdatesAndStatusesCabinetWithAudit() throws Exception {
        mockMvc.perform(post("/api/admin/cabinets")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" Cabinet Gamma \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cabinet Gamma"))
                .andExpect(jsonPath("$.active").value(true));

        Cabinet created = cabinetRepository.findAll().stream()
                .filter(cabinet -> cabinet.getName().equals("Cabinet Gamma"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/api/admin/cabinets")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"cabinet gamma\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(put("/api/admin/cabinets/" + created.getId())
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Cabinet Gamma Modifie\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cabinet Gamma Modifie"));

        mockMvc.perform(patch("/api/admin/cabinets/" + created.getId() + "/status")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        assertThat(auditLogRepository.findAll())
                .extracting(log -> log.getAction())
                .contains(AuditAction.CREATED, AuditAction.UPDATED, AuditAction.DEACTIVATED);
    }

    @Test
    void rejectsInvalidPayloadMissingAndInvalidIds() throws Exception {
        mockMvc.perform(post("/api/admin/cabinets")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.name").exists());

        mockMvc.perform(get("/api/admin/cabinets/not-a-uuid").with(adminJwt()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/admin/cabinets/" + java.util.UUID.randomUUID()).with(adminJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void restrictsMutationToAdminAndRejectsInactiveCabinet() throws Exception {
        mockMvc.perform(post("/api/admin/cabinets")
                        .with(jwt().jwt(token -> token.subject(doctor.getId().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_MEDECIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Cabinet Interdit\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/admin/cabinets/" + activeCabinet.getId() + "/status")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/me")
                        .with(adminJwt()))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().jwt(token -> token.subject(admin.getId().toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
