package com.LabResourceUtilizationPlatform.Dtos.Response;

import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentBookingResponse {

    private String equipmentName;
    private String bookedBy;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}