package com.gabriel.springboot.app.menuflow.config;

import com.gabriel.springboot.app.menuflow.models.entities.Role;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import com.gabriel.springboot.app.menuflow.repositories.RoleRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the roles catalog and a first ADMIN account for local development.
 * Only runs with SPRING_PROFILES_ACTIVE=dev (never in prod) and only when
 * there is no ADMIN user yet, so it is safe to leave the app running.
 *
 * Default credentials (change/remove before any real deployment):
 *   username: admin
 *   password: Admin123!
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@menuflow.local";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin123!";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(Role.of(roleName)));
        }

        if (userRepository.existsByUsername(DEFAULT_ADMIN_USERNAME)) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();

        User admin = User.of(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_EMAIL);
        admin.updatePassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        admin.addRole(adminRole);

        userRepository.save(admin);

        log.warn("Dev seed: created default ADMIN user '{}' with password '{}' - do not use in production",
                DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
    }
}
