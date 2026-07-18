package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityResponse {

    private String institutionName;
    private String activity;
    private String status;
    private LocalDateTime createdAt;
}
