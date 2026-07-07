package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInstitutionRequest {
    @NotBlank(message = "Institution code is required")
    private String code;

    @NotBlank(message = "Institution name is required")
    private String name;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private String website;
}
