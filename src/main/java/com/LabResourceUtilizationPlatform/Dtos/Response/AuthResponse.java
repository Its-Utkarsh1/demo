package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String message;
}
