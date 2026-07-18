package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAdminDashboardResponse {

    private Long totalInstitutions;
    private Long totalDepartments;
    private Long totalLabs;
    private Long totalEquipment;
    private Long totalUsers;
    private List<RecentActivityResponse> recentActivities;
}
