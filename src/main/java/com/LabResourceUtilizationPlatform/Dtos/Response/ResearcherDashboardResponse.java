package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearcherDashboardResponse {

    private Long totalBookings;
    private Long activeBookings;
    private Long completedBookings;
    private Long availableEquipment;
}
