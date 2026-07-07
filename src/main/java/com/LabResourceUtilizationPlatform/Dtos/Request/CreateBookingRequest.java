package com.LabResourceUtilizationPlatform.Dtos.Request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private Long bookingId;
    private String equipmentName;
    private String bookedBy;
    private String institutionName;
    private String departmentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingStatus;
    private LocalDateTime createdAt;
}
