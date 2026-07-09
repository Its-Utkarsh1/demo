package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabResponse {

    private String labName;
    private String labCode;
    private String location;
    private Integer userCapacity;
    private String status;
    private String institution;
    private String department;
}
