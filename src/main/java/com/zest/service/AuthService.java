package com.zest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zest.dto.LoginRequest;
import com.zest.dto.LoginResponse;
import com.zest.dto.RegisterRequest;
import com.zest.model.Role;
import com.zest.model.User;
import com.zest.repository.UserRepository;
import com.zest.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public String register(RegisterRequest request) {

        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin registration is not allowed");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Send registration confirmation email
        log.info("Triggering registration confirmation email for: {}", user.getEmail());
        boolean emailSent = emailService.sendRegistrationConfirmation(user.getEmail(), user.getName());
        if (emailSent) {
            log.info("Registration confirmation email sent successfully to: {}", user.getEmail());
        } else {
            log.warn("Failed to send registration confirmation email to: {}", user.getEmail());
        }

        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
        throw new RuntimeException("Your account is blocked by admin");
    }

    if (user.getRole() == Role.ORGANIZER &&
        Boolean.FALSE.equals(user.getApproved())) {

        throw new RuntimeException("Organizer not approved by admin yet");
    }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(
                "Login successful",
                user.getUserId(),
                user.getRole().name(),
                token
        );
    }

    public String createAdminUser() {
        // Check if admin already exists
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        if (!admins.isEmpty()) {
            throw new RuntimeException("Admin user already exists");
        }

        // Use a default password
        String defaultPassword = "admin123";
        
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@zest.com");
        admin.setPassword(passwordEncoder.encode(defaultPassword));
        admin.setRole(Role.ADMIN);
        admin.setIsActive(true);
        admin.setApproved(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        userRepository.save(admin);
        
        return "Admin user created successfully. Email: admin@zest.com, Password: " + defaultPassword;
    }
}

