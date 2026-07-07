package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String equipmentName;
    private String bookedBy;
    private String institutionName;
    private String departmentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingStatus;
    private String purpose;
    private LocalDateTime createdAt;
}
