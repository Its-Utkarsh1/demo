package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentResponse {

    private String name;

    private String institutionCode;

    private String institutionName;
}
