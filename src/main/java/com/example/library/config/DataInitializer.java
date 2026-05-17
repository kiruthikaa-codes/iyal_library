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
        
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .name("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .isProfilePublic(true)
                    .isActive(true)
                    .build();
            
            userRepository.save(admin);
            log.info("✅ Default admin user created: admin@iyal.com / admin123");
        } else {
            // Update existing user to admin if needed
            userRepository.findByEmail(adminEmail).ifPresent(user -> {
                if (user.getRole() != User.Role.ADMIN) {
                    user.setRole(User.Role.ADMIN);
                    userRepository.save(user);
                    log.info("✅ Updated user to ADMIN role: admin@iyal.com");
                } else {
                    log.info("✅ Admin user already exists: admin@iyal.com");
                }
            });
        }
    }
}
