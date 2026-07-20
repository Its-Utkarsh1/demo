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
    private String model;
    private String description;
    private String specifications;
    private String imageUrl;
    private String labCode;
    private String status;
    private String lab;
    private String department;
    private String institution;
    private Integer availableQuantity;
}
