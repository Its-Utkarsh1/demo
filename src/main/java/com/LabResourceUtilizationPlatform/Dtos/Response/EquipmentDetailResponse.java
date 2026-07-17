package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDetailResponse {
        private String equipmentName;
        private String equipmentCode;
        private String manufacturer;
        private String model;
        private String description;
        private String specifications;
        private String imageUrl;
        private LocalDate purchaseDate;
        private BigDecimal price;
        private Integer quantity;
        private String status;
        private String lab;
        private String department;
        private String institution;
        private Integer availableQuantity;
}
