package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest{
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
    private RoleName role;

    @NotNull(message = "Institution name is required")
    private String institutionName;

    @NotNull(message = "Institution ID is required")
    private Long institutionId;

    @NotNull(message = "Department name is required")
    private String departmentName;

}
