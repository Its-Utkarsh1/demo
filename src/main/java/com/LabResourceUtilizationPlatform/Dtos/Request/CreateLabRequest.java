package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.Enum.LabStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabRequest {

    @NotBlank(message = "Lab name is required.")
    private String labName;

    @NotBlank(message = "Lab code is required.")
    private String labCode;

    @NotBlank(message = "location is required.")
    private String location;

    @NotNull(message = "Lab user capacity is required.")
    private Integer userCapacity;

    @NotNull(message = "Lab status is required.")
    private LabStatus status;

    @NotBlank(message = "Institution name is required.")
    private String institutionName;

    @NotBlank(message = "Institution code is required.")
    private String institutionCode;

    @NotBlank(message = "Department name required.")
    private String departmentName;
}
