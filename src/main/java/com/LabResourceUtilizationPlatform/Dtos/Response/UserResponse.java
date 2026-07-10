package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String fullName;
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
    private String role;
    private String institution;
    private String department;
}
