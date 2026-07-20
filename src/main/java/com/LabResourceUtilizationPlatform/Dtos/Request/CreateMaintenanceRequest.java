package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateMaintenanceRequest {

    @NotBlank
    private String equipmentCode;

    @NotBlank
    private String maintenanceType;

    @NotNull
    @Future
    private LocalDate scheduledDate;

    @NotBlank
    private String technicianEmail;

    @NotBlank
    private String description;
}