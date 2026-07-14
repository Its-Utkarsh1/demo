package com.LabResourceUtilizationPlatform.Dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyUtilizationResponse {

    private List<Integer> utilization;

    public List<Integer> getUtilization() {
        return utilization;
    }

    public void setUtilization(List<Integer> utilization) {
        this.utilization = utilization;
    }
}
