package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipmentRequest {

    @NotBlank(message = "Equipment name is required.")
    private String equipmentName;

    @NotBlank(message = "Equipment code is required.")
    private String equipmentCode;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    @NotNull(message = "Equipment status is required.")
    private EquipmentStatus status;

    @NotBlank(message = "Lab code is required.")
    private String labCode;

    @NotBlank(message = "Institution code is required.")
    private String institutionCode;
}
