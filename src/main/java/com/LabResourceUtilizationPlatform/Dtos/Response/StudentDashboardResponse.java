package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {

    private Long totalBookings;
    private Long upcomingBookings;
    private Long completedBookings;
    private Long cancelledBookings;
}