package com.LabResourceUtilizationPlatform.Controller;
import com.LabResourceUtilizationPlatform.Dtos.Request.CreateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.LabResponse;
import com.LabResourceUtilizationPlatform.Service.LabService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/labs")
public class LabController {

    private final LabService labService;

    @PreAuthorize("hasAnyRole('INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<LabResponse> createLab(
            @Valid @RequestBody CreateLabRequest request) {

        LabResponse response = labService.createLab(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{institutionCode}/{labCode}")
    public ResponseEntity<LabResponse> getLabByCode(
            @PathVariable String institutionCode,
            @PathVariable String labCode) {

        return ResponseEntity.ok(
                labService.getLabByCode(labCode, institutionCode));
    }

    @GetMapping("/department")
    public ResponseEntity<List<LabResponse>> getLabsByDepartment(
            @RequestParam String institutionCode,
            @RequestParam String departmentName) {

        return ResponseEntity.ok(
                labService.getLabsByDepartment(
                        institutionCode,
                        departmentName));
    }

    @GetMapping("/{institutionCode}")
    public ResponseEntity<List<LabResponse>> getAllLabs(
            @PathVariable String institutionCode) {

        return ResponseEntity.ok(
                labService.getAllLabs(institutionCode));
    }

    @PreAuthorize("hasAnyRole('INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    @PutMapping
    public ResponseEntity<LabResponse> updateLab(
            @Valid @RequestBody UpdateLabRequest request) {

        return ResponseEntity.ok(
                labService.updateLab(request));
    }

    @PreAuthorize("hasAnyRole('INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    @DeleteMapping("/{institutionCode}/{labCode}")
    public ResponseEntity<String> deleteLab(
            @PathVariable String institutionCode,
            @PathVariable String labCode) {

        labService.deleteLab(labCode, institutionCode);
        return ResponseEntity.ok("Lab deleted successfully.");
    }
}