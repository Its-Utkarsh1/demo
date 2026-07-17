package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentRequest {

    @NotBlank(message = "Equipment code is required.")
    private String equipmentCode;

    private String newEquipmentCode;

    @NotBlank(message = "Equipment name is required.")
    private String equipmentName;

    @NotBlank(message = "Manufacturer is required.")
    private String manufacturer;

    @NotBlank(message = "Model is required.")
    private String model;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    private String description;

    @Size(max = 5000, message = "Specifications cannot exceed 5000 characters.")
    private String specifications;

    private String imageUrl;

    @PastOrPresent(message = "Purchase date cannot be in the future.")
    private LocalDate purchaseDate;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    private BigDecimal price;

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