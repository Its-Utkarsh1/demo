package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Response.TechnicianDashboardResponse;
import com.LabResourceUtilizationPlatform.Dtos.Response.WeeklyUtilizationResponse;

public interface DashBoardService {

    WeeklyUtilizationResponse getWeeklyUtilization();
    TechnicianDashboardResponse getTechnicianDashboard();
}
