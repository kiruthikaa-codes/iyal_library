package com.example.library.config;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminIfNotExists();
    }
    
    private void createDefaultAdminIfNotExists() {
        String adminEmail = "admin@iyal.com";
        String adminPassword = "admin123";

        // Check if admin user exists
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
            user -> {
                // Update existing user: reset password and ensure ADMIN role
                user.setPassword(passwordEncoder.encode(adminPassword));
                user.setRole(User.Role.ADMIN);
                user.setIsActive(true);
                userRepository.save(user);
                log.info("✅ Updated admin user: admin@iyal.com / admin123 (password reset)");
            },
            () -> {
                // Create new admin user
                User admin = User.builder()
                        .name("Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(User.Role.ADMIN)
                        .isProfilePublic(true)
                        .isActive(true)
                        .build();

                userRepository.save(admin);
                log.info("✅ Created new admin user: admin@iyal.com / admin123");
            }
        );
    }
}
