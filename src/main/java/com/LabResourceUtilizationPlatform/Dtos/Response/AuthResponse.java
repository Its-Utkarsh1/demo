package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String message;
}
