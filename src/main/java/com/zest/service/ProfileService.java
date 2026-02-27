package com.zest.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.zest.dto.ProfileResponse;
import com.zest.dto.ProfileUpdateRequest;
import com.zest.model.User;
import com.zest.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return mapToProfileResponse(user);
    }

    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Update profile fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation().trim());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    private ProfileResponse mapToProfileResponse(User user) {
        ProfileResponse response = new ProfileResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBio(user.getBio());
        response.setLocation(user.getLocation());
        response.setIsActive(user.getIsActive());
        response.setApproved(user.getApproved());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}