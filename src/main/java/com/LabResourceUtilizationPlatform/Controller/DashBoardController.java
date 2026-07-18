package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Response.*;
import com.LabResourceUtilizationPlatform.Service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashboardService;

    @GetMapping("/weekly-utilization")
    public ResponseEntity<WeeklyUtilizationResponse> getWeeklyUtilization() {

        return ResponseEntity.ok(
                dashboardService.getWeeklyUtilization()
        );
    }

    @GetMapping("/student")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard() {

        return ResponseEntity.ok(
                dashboardService.getStudentDashboard()
        );
    }

    @GetMapping("/researcher")
    public ResponseEntity<ResearcherDashboardResponse> getResearcherDashboard() {

        return ResponseEntity.ok(
                dashboardService.getResearcherDashboard()
        );
    }

    @GetMapping("/faculty")
    public ResponseEntity<FacultyDashboardResponse> getFacultyDashboard() {

        return ResponseEntity.ok(
                dashboardService.getFacultyDashboard()
        );
    }

    @GetMapping("/technician")
    public ResponseEntity<TechnicianDashboardResponse> getTechnicianDashboard() {

        return ResponseEntity.ok(
                dashboardService.getTechnicianDashboard()
        );
    }

    @GetMapping("/lab-manager")
    public ResponseEntity<LabManagerDashboardResponse> getLabManagerDashboard() {

        return ResponseEntity.ok(
                dashboardService.getLabManagerDashboard()
        );
    }

    @GetMapping("/department-head")
    public ResponseEntity<DepartmentHeadDashboardResponse> getDepartmentHeadDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDepartmentHeadDashboard()
        );
    }

    @GetMapping("/institution-admin")
    public ResponseEntity<InstitutionAdminDashboardResponse> getInstitutionAdminDashboard() {

        return ResponseEntity.ok(
                dashboardService.getInstitutionAdminDashboard()
        );
    }

    @GetMapping("/system-admin")
    public ResponseEntity<SystemAdminDashboardResponse> getSystemAdminDashboard() {

        return ResponseEntity.ok(
                dashboardService.getSystemAdminDashboard()
        );
    }
}