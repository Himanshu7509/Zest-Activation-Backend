package com.zest.dto;

import java.time.LocalDateTime;

import com.zest.model.Role;

import lombok.Data;

@Data
public class ProfileResponse {
    private String userId;
    private String name;
    private String email;
    private Role role;
    private String profileImageUrl;
    private String phoneNumber;
    private String bio;
    private String location;
    private Boolean isActive;
    private Boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}