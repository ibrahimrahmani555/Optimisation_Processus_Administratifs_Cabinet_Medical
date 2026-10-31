package ma.cabinetmedical.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cabinetmedical.cabinet.Cabinet;
import ma.cabinetmedical.cabinet.CabinetRepository;
import ma.cabinetmedical.user.Role;
import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired CabinetRepository cabinetRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private User admin;
    private Cabinet cabinet;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        cabinetRepository.deleteAll();
        cabinet = cabinetRepository.save(new Cabinet("Cabinet Test"));
        admin = userRepository.save(new User(
                cabinet, "Ibrahim", "Admin", "admin@cabinet.ma", passwordEncoder.encode("Password123!"), Role.ADMIN
        ));
    }

    @Test
    void loginReturnsJwtAndSafeUserData() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ADMIN@CABINET.MA\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.user.cabinetName").value("Cabinet Test"))
                .andReturn().getResponse().getContentAsString();

        assertThat(response).doesNotContain("passwordHash").doesNotContain("Password123!");
    }

    @Test
    void currentUserRequiresAndAcceptsBearerToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());

        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@cabinet.ma\",\"password\":\"Password123!\"}"))
                .andReturn().getResponse().getContentAsString();
        JsonNode login = objectMapper.readTree(loginBody);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + login.get("accessToken").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admin.getId().toString()))
                .andExpect(jsonPath("$.email").value("admin@cabinet.ma"));
    }

    @Test
    void invalidPasswordIsRejectedWithGenericMessage() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@cabinet.ma\",\"password\":\"incorrect\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("E-mail ou mot de passe incorrect"));
    }

    @Test
    void inactiveUserAndInactiveCabinetAreRejected() throws Exception {
        admin.setActive(false);
        userRepository.save(admin);
        expectLoginRejected();

        admin.setActive(true);
        cabinet.setActive(false);
        cabinetRepository.save(cabinet);
        userRepository.save(admin);
        expectLoginRejected();
    }

    @Test
    void invalidPayloadReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Donnees invalides"))
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    private void expectLoginRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@cabinet.ma\",\"password\":\"Password123!\"}"))
                .andExpect(status().isUnauthorized());
    }
}
