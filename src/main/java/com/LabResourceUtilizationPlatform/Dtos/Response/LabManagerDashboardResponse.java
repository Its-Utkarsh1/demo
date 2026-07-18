package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabManagerDashboardResponse {

    private Long totalEquipment;
    private Long equipmentInUse;
    private Long maintenanceDue;
    private Double utilizationRate;

    private Long availableEquipment;
    private Long bookedEquipment;
    private Long maintenanceEquipment;
    private Long outOfServiceEquipment;

    private Long pendingBookings;
    private Long approvedToday;
    private Long rejectedBookings;

    private List<MaintenanceResponse> todayMaintenance;
}
