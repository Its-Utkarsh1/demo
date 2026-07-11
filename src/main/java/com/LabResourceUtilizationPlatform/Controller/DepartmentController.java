package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.DepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;
import com.LabResourceUtilizationPlatform.Service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {

        DepartmentResponse response = departmentService.createDepartment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<DepartmentResponse> getDepartmentByName(
            @Valid @RequestBody DepartmentRequest request) {

        return ResponseEntity.ok(
                departmentService.getDepartmentByName(request));
    }

    @GetMapping("/{institutionCode}")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments(
            @PathVariable String institutionCode) {

        return ResponseEntity.ok(
                departmentService.getAllDepartments(institutionCode));
    }

    @PutMapping("/{newName}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @RequestBody DepartmentRequest request,
            @PathVariable String newName) {

        return ResponseEntity.ok(
                departmentService.updateDepartment(request, newName));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteDepartment(
            @RequestBody DepartmentRequest request) {

        departmentService.deleteDepartment(request);
        return ResponseEntity.ok("Department deleted successfully.");
    }
}