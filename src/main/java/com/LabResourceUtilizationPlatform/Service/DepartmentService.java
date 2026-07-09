package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.DepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse getDepartmentByName(DepartmentRequest request);

    List<DepartmentResponse> getAllDepartments(String institutionCode);

    DepartmentResponse updateDepartment(
            DepartmentRequest request,
            String newName);

    void deleteDepartment(DepartmentRequest request);
}
