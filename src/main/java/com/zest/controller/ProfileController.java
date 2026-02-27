package com.zest.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zest.dto.ProfileResponse;
import com.zest.dto.ProfileUpdateRequest;
import com.zest.service.ProfileImageS3Service;
import com.zest.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileImageS3Service profileImageS3Service;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        String userId = authentication.getName(); // This will be the email from JWT
        log.info("Fetching profile for user: {}", userId);
        
        try {
            ProfileResponse profile = profileService.getProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error fetching profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            ProfileUpdateRequest request) {
        
        String userId = authentication.getName();
        log.info("Updating profile for user: {}", userId);
        
        try {
            ProfileResponse updatedProfile = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            log.error("Error updating profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/image")
    public ResponseEntity<String> uploadProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image) {
        
        String userId = authentication.getName();
        log.info("Uploading profile image for user: {}", userId);
        
        try {
            profileImageS3Service.uploadAndSaveProfileImage(image, userId);
            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (IOException e) {
            log.error("IO Error uploading profile image for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to upload image");
        } catch (RuntimeException e) {
            log.error("Error uploading profile image for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/image")
    public ResponseEntity<String> deleteProfileImage(Authentication authentication) {
        String userId = authentication.getName();
        log.info("Deleting profile image for user: {}", userId);
        
        try {
            profileImageS3Service.deleteProfileImage(userId);
            return ResponseEntity.ok("Profile image deleted successfully");
        } catch (RuntimeException e) {
            log.error("Error deleting profile image for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}