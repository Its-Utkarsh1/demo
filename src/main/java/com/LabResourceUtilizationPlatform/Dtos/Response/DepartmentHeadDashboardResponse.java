package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentHeadDashboardResponse {

    private Long totalLabs;
    private Long totalEquipment;
    private Long activeBookings;
    private Long departmentUsers;
    private List<RecentBookingResponse> recentBookings;
}
