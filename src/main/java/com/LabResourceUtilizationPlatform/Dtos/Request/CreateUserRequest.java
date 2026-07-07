package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest{

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Registration ID is required")
    private String registrationId;

    @NotNull(message = "Role is required")
    private Long roleId;

    @NotNull(message = "Institution ID is required")
    private Long institutionId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
