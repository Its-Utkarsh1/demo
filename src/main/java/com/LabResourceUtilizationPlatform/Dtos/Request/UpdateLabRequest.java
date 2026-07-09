package com.LabResourceUtilizationPlatform.Dtos.Request;


import com.LabResourceUtilizationPlatform.Entity.Enum.LabStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLabRequest {

    // Current lab identifier
    @NotBlank(message = "Lab code is required")
    private String labCode;

    @NotBlank(message = "Institution code is required")
    private String institutionCode;

    // Optional: if changing the lab code
    private String newLabCode;

    @NotBlank(message = "Lab name is required")
    private String labName;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "User capacity is required")
    @Min(value = 1, message = "User capacity must be at least 1")
    private Integer userCapacity;

    @NotNull(message = "Status is required")
    private LabStatus status;
}