package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDashboardResponse {

    private Long totalBookings;
    private Long upcomingBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long pendingApprovals;
    private Long departmentEquipment;
    private List<RecentBookingResponse> recentBookings;
}