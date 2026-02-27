package com.zest.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String userId;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private Boolean isActive = true;

    private Boolean approved = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Profile fields
    private String profileImageUrl;
    private String profileImageS3Key;
    private String phoneNumber;
    private String bio;
    private String location;
    private LocalDateTime updatedAt = LocalDateTime.now();
}

