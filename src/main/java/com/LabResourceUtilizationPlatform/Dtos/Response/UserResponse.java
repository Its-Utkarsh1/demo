package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private String fullName;
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
    private String registrationId;
    private String role;
    private String institution;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
