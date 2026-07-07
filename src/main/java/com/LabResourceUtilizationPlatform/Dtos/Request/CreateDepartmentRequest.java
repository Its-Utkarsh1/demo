package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    @NotNull(message = "Institution code is required")
    private String institutionCode;
}
