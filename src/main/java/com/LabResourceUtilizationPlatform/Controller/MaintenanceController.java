package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateMaintenanceRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.MaintenanceResponse;
import com.LabResourceUtilizationPlatform.Service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@CrossOrigin
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LAB_MANAGER','SYSTEM_ADMIN')")
    public ResponseEntity<MaintenanceResponse> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequest request) {

        return ResponseEntity.ok(
                maintenanceService.createMaintenance(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceResponse>> getAllMaintenance() {

        return ResponseEntity.ok(
                maintenanceService.getAllMaintenance()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> getMaintenance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService.getMaintenance(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','SYSTEM_ADMIN')")
    public ResponseEntity<MaintenanceResponse> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody CreateMaintenanceRequest request) {

        return ResponseEntity.ok(
                maintenanceService.updateMaintenance(id, request)
        );
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','LAB_TECHNICIAN','SYSTEM_ADMIN')")
    public ResponseEntity<String> completeMaintenance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService.completeMaintenance(id)
        );
    }
    @GetMapping("/my-maintenance")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<List<MaintenanceResponse>> getMyMaintenance() {

        return ResponseEntity.ok(
                maintenanceService.getMyMaintenance()
        );
    }
    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<String> startMaintenance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                maintenanceService.startMaintenance(id)
        );
    }
}