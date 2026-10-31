package ma.cabinetmedical.config;

import ma.cabinetmedical.cabinet.Cabinet;
import ma.cabinetmedical.cabinet.CabinetRepository;
import ma.cabinetmedical.user.Role;
import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {
    private final CabinetRepository cabinetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String cabinetName;
    private final String adminEmail;
    private final String adminPassword;

    public BootstrapAdminInitializer(
            CabinetRepository cabinetRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.cabinet-name:}") String cabinetName,
            @Value("${app.bootstrap.admin-email:}") String adminEmail,
            @Value("${app.bootstrap.admin-password:}") String adminPassword
    ) {
        this.cabinetRepository = cabinetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cabinetName = cabinetName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(cabinetName) || !StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }
        if (adminPassword.length() < 12) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD doit contenir au moins 12 caracteres");
        }
        if (userRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
            return;
        }

        Cabinet cabinet = cabinetRepository.save(new Cabinet(cabinetName.trim()));
        userRepository.save(new User(
                cabinet,
                "Administrateur",
                "Cabinet",
                adminEmail,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN
        ));
    }
}
