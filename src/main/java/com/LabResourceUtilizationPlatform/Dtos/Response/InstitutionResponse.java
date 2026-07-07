package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InstitutionResponse {

    private String code;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private String website;

}
