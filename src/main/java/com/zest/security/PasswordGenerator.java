package com.zest.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.zest.model.Role;
import com.zest.model.User;
import com.zest.repository.UserRepository;

@Component
public class PasswordGenerator implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PasswordGenerator(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            // Create default admin user
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@zest.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            admin.setApproved(true);
            
            userRepository.save(admin);
            System.out.println("Default admin user created:");
            System.out.println("Email: admin@zest.com");
            System.out.println("Password: admin123");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}
