package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInstitutionRequest {
    @NotBlank(message = "Institution Name is required")
    private String name;

    @NotBlank(message = "Institution code is required")
    private String code;

    @NotBlank(message = "Institution Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Institution Phone number is requied")
    private String phoneNumber;

    @NotBlank(message = "Institution Address is required")
    private String address;

    private String website;
}
