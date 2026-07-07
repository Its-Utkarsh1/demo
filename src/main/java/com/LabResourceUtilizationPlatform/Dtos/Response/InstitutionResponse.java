package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionResponse {

    private String code;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private String website;

    private List<DepartmentResponse> departments;

}
