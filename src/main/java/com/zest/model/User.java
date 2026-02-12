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

    private Boolean isActive;

    private LocalDateTime createdAt;
}

