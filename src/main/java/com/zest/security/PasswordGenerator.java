package com.zest.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    public PasswordGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Encoded Admin Password: " + encodedPassword);
    }
}
