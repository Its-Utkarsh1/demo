package com.LabResourceUtilizationPlatform.Dtos.Response;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentResponse {

    private String equipmentName;

    private String equipmentCode;

    private Integer quantity;

    private String status;

    private String lab;

    private String department;

    private String institution;
}
