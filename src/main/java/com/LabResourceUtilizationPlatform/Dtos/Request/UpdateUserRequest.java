package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.Enum.RoleName;
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

    @Email(message = "Invalid email format")
    private String newEmail;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private RoleName role;

    @NotNull(message = "Institution name is required")
    private String institutionName;

    @NotNull(message = "Institution code is required")
    private String institutionCode;

    @NotNull(message = "Department name is required")
    private String departmentName;

}
