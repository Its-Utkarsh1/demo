package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechnicianDashboardResponse {

    private long availableEquipment;
    private long inUseEquipment;
    private long underMaintenance;
    private long outOfService;
}