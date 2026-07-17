package com.LabResourceUtilizationPlatform.Dtos.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotBlank(message = "Institution code is required.")
    private String institutionCode;

    @NotBlank(message = "Lab code is required.")
    private String labCode;

    @NotBlank(message = "Equipment code is required.")
    private String equipmentCode;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    @NotNull(message = "Start time is required.")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required.")
    private LocalDateTime endTime;

    @NotBlank(message = "Purpose is required.")
    private String purpose;
}