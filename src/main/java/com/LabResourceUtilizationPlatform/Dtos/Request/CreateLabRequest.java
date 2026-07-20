package com.LabResourceUtilizationPlatform.Dtos.Request;

import com.LabResourceUtilizationPlatform.Entity.Enum.LabStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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

    @NotBlank(message = "Location is required.")
    private String location;

    @NotNull(message = "User capacity is required.")
    @Min(value = 1, message = "User capacity must be at least 1.")
    private Integer userCapacity;

    @NotNull(message = "Lab status is required.")
    private LabStatus status;

    @NotBlank(message = "Institution name is required.")
    private String institutionName;

    @NotBlank(message = "Institution code is required.")
    private String institutionCode;

    @NotBlank(message = "Department name is required.")
    private String departmentName;

    @NotBlank(message = "Lab Manager email is required.")
    @Email(message = "Invalid email format.")
    private String managerEmail;
}
