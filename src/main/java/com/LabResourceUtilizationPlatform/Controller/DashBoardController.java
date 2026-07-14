package com.LabResourceUtilizationPlatform.Controller;


import com.LabResourceUtilizationPlatform.Dtos.Response.WeeklyUtilizationResponse;
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
}
