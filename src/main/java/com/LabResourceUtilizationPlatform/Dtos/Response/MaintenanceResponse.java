package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MaintenanceResponse {

    private Long id;

    private String equipmentName;

    private String equipmentCode;

    private String maintenanceType;

    private LocalDate scheduledDate;

    private String technicianName;

    private String description;

    private String status;
}