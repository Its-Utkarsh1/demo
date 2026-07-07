package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse getDepartmentById(Long id);

    List<DepartmentResponse> getAllDepartments();

    List<DepartmentResponse> getDepartmentsByInstitution(String institutionCode);

    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);

    void deleteDepartment(Long id);
}
