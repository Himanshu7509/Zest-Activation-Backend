package com.zest.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String phoneNumber;
    private String bio;
    private String location;
}