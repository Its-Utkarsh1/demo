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

    private InstitutionRepository institutionRepository;
    private ModelMapper modelMapper;
    private DepartmentRepository departmentRepository;


    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {

        Institution institution = institutionRepository.findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        if (departmentRepository.existsByNameAndInstitution(request.getName(), institution)) {
            throw new RuntimeException("Department already exists in this institution.");
        }
        Department department = Department.builder()
                .name(request.getName())
                .institution(institution)
                .build();
        Department savedDepartment = departmentRepository.save(department);

        DepartmentResponse response = modelMapper.map(savedDepartment, DepartmentResponse.class);
        response.setInstitutionCode(savedDepartment.getInstitution().getCode());
        response.setInstitutionName(savedDepartment.getInstitution().getName());

        return response;
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(()-> new RuntimeException("Department not found."));
        DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);
        response.setInstitutionCode(department.getInstitution().getCode());
        response.setInstitutionName(department.getInstitution().getName());

        return response;
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> {
                    DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);
                    response.setInstitutionCode(department.getInstitution().getCode());
                    response.setInstitutionName(department.getInstitution().getName());
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
                    response.setInstitutionCode(institution.getCode());
                    response.setInstitutionName(institution.getName());
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

        if (departmentRepository.existsByNameAndInstitution(request.getName(), institution)
                && (!department.getName().equals(request.getName())
                || !department.getInstitution().getId().equals(institution.getId()))) {

            throw new RuntimeException("Department already exists in this institution.");
        }

        department.setName(request.getName());
        department.setInstitution(institution);

        Department updatedDepartment = departmentRepository.save(department);

        DepartmentResponse response = modelMapper.map(updatedDepartment, DepartmentResponse.class);
        response.setInstitutionCode(updatedDepartment.getInstitution().getCode());
        response.setInstitutionName(updatedDepartment.getInstitution().getName());

        return response;
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found.");
        }
        departmentRepository.deleteById(id);
    }
}
