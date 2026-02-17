package com.zest.service;

import java.time.LocalDateTime;

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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

}

