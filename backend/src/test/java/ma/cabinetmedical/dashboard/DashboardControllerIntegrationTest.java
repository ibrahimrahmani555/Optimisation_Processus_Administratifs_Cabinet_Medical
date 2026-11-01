package ma.cabinetmedical.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cabinetmedical.cabinet.Cabinet;
import ma.cabinetmedical.cabinet.CabinetRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired CabinetRepository cabinetRepository;
    @Autowired PatientRepository patientRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private User admin;
    private User doctor;
    private Cabinet activeCabinet;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
        userRepository.deleteAll();
        cabinetRepository.deleteAll();

        activeCabinet = cabinetRepository.save(new Cabinet("Cabinet Actif"));
        Cabinet inactiveCabinet = cabinetRepository.save(new Cabinet("Cabinet Inactif"));
        inactiveCabinet.setActive(false);
        cabinetRepository.save(inactiveCabinet);
        admin = userRepository.save(new User(activeCabinet, "Admin", "Test", "admin-dashboard@test.ma",
                passwordEncoder.encode("Password123!"), Role.ADMIN));
        doctor = userRepository.save(new User(activeCabinet, "Docteur", "Test", "doctor-dashboard@test.ma",
                passwordEncoder.encode("Password123!"), Role.MEDECIN));
        userRepository.save(new User(inactiveCabinet, "Secretaire", "Test", "secretary-dashboard@test.ma",
                passwordEncoder.encode("Password123!"), Role.SECRETAIRE));
        patientRepository.save(new Patient(activeCabinet, "Patient", "Test"));
    }

    @Test
    void statisticsAreCalculatedFromPersistedData() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/statistics")
                        .with(jwt().jwt(token -> token.subject(admin.getId().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCabinets").value(2))
                .andExpect(jsonPath("$.activeCabinets").value(1))
                .andExpect(jsonPath("$.inactiveCabinets").value(1))
                .andExpect(jsonPath("$.doctors").value(1))
                .andExpect(jsonPath("$.secretaries").value(1))
                .andExpect(jsonPath("$.patients").value(1));
    }

    @Test
    void dashboardIsRestrictedToAdministrators() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/statistics")
                        .with(jwt().jwt(token -> token.subject(doctor.getId().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_MEDECIN"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void emptyActivityStateAndMonthlySeriesAreReturned() throws Exception {
        String response = mockMvc.perform(get("/api/admin/dashboard/recent-activities")
                        .with(jwt().jwt(token -> token.subject(admin.getId().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn().getResponse().getContentAsString();

        JsonNode activities = objectMapper.readTree(response);
        org.assertj.core.api.Assertions.assertThat(activities).isEmpty();

        mockMvc.perform(get("/api/admin/dashboard/cabinets-evolution?from=2026-01-01&to=2026-03-31")
                        .with(jwt().jwt(token -> token.subject(admin.getId().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
