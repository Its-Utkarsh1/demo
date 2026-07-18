package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionAdminDashboardResponse {

    private Long totalDepartments;
    private Long totalUsers;
    private Long totalLabs;
    private Long totalEquipment;
    private Long monthlyBookings;

    private Long totalFaculty;
    private Long totalResearchers;
    private Long totalStudents;

    private Long availableEquipment;
    private Long bookedEquipment;
    private Long maintenanceEquipment;

    private List<RecentActivityResponse> recentActivities;
}
