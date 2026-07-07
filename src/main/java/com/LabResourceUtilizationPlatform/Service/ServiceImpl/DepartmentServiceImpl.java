package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateDepartmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Repository.DepartmentRepository;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final InstitutionRepository institutionRepository;
    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;


    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {

        Institution institution = institutionRepository.findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        if (departmentRepository.existsByName(request.getName())){
            throw new RuntimeException("Department already exists in this institution.");
        }
        Department department = Department.builder()
                .name(request.getName())
                .institution(institution)
                .build();
        Department savedDepartment = departmentRepository.save(department);
        return modelMapper.map(savedDepartment, DepartmentResponse.class);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Department not found."));
        return modelMapper.map(department, DepartmentResponse.class);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> {
                    DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);
                    return response;
                })
                .toList();
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByInstitution(String institutionCode) {
        Institution institution = institutionRepository.findByCode(institutionCode).orElseThrow(()->new RuntimeException("Institution not found"));
        return departmentRepository.findByInstitution(institution)
                .stream()
                .map(department ->{
                    DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);
                    return response;
                })
                .toList();
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found."));

        Institution institution = institutionRepository.findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        if (departmentRepository.existsByName(request.getName())
                && (!department.getName().equals(request.getName())
                || !department.getInstitution().getId().equals(institution.getId()))) {

            throw new RuntimeException("Department already exists in this institution.");
        }
        department.setName(request.getName());
        department.setInstitution(institution);
        Department updatedDepartment = departmentRepository.save(department);
        return modelMapper.map(updatedDepartment, DepartmentResponse.class);
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found.");
        }
        departmentRepository.deleteById(id);
    }
}
