package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.DepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Repository.DepartmentRepository;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final InstitutionRepository institutionRepository;
    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {

        Institution institution = institutionRepository.findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        if (departmentRepository.existsByNameAndInstitution_Code(request.getName(), request.getInstitutionCode())) {
            throw new RuntimeException("Department already exists in this institution.");
        }
        Department department = Department.builder()
                .name(request.getName())
                .institution(institution)
                .build();
        Department savedDepartment = departmentRepository.save(department);
        logger.info("Department created: {}", savedDepartment.getName());
        return mapToResponse(savedDepartment);    }

    @Override
    @Cacheable(value = "departments", key = "#request.institutionCode + ':' + #request.name")
    public DepartmentResponse getDepartmentByName(DepartmentRequest request) {
        Department department = departmentRepository.findByNameAndInstitution_Code(request.getName(),
                request.getInstitutionCode()).orElseThrow(() -> new RuntimeException("Department not found."));
        return mapToResponse(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments(String institutionCode) {
        return departmentRepository.findByInstitution_Code(institutionCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "departments", key = "#request.institutionCode + ':' + #request.name")
    public DepartmentResponse updateDepartment(DepartmentRequest request,String newName) {
        Department department = departmentRepository.findByNameAndInstitution_Code(request.getName(),request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Department not found."));


        if (!department.getName().equals(newName)
                && departmentRepository.existsByNameAndInstitution_Code(newName, request.getInstitutionCode())) {

            throw new RuntimeException("Department already exists in this institution.");
        }
        department.setName(newName);
        Department updatedDepartment = departmentRepository.save(department);
        logger.info("Department updated: {}", updatedDepartment.getName());
        return mapToResponse(updatedDepartment);
    }

    @Override
    @CacheEvict(value = "departments", key = "#request.institutionCode + ':' + #request.name")
    public void deleteDepartment(DepartmentRequest request) {

        Department department = departmentRepository
                .findByNameAndInstitution_Code(
                        request.getName(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Department not found."));

        departmentRepository.delete(department);
        logger.info("Department deleted: {}", department.getName());
    }

    private DepartmentResponse mapToResponse(Department department) {
        return modelMapper.map(department, DepartmentResponse.class);
    }
}
