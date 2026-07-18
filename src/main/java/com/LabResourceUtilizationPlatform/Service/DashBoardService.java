package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Response.*;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashBoardService {

    WeeklyUtilizationResponse getWeeklyUtilization();
    TechnicianDashboardResponse getTechnicianDashboard();
    StudentDashboardResponse getStudentDashboard();

    ResearcherDashboardResponse getResearcherDashboard();

    FacultyDashboardResponse getFacultyDashboard();

    LabManagerDashboardResponse getLabManagerDashboard();

    DepartmentHeadDashboardResponse getDepartmentHeadDashboard();

    InstitutionAdminDashboardResponse getInstitutionAdminDashboard();

    SystemAdminDashboardResponse getSystemAdminDashboard();
}
