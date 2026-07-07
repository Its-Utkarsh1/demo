package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final ModelMapper modelMapper;

    @Override
    public InstitutionResponse createInstitution(CreateInstitutionRequest request) {
        if(institutionRepository.existsByCode(request.getCode())){
            throw  new RuntimeException("Institution code already exists.");
        }
        if(institutionRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Institution email already exists.");
        }
        if (institutionRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Institution phone number already exists.");
        }
        Institution institution = modelMapper.map(request,Institution.class);
        return modelMapper.map(institutionRepository.save(institution),InstitutionResponse.class);
    }

    @Override
    public InstitutionResponse getInstitutionById(Long id) {
        Institution institution = institutionRepository.findById(id).orElseThrow(() -> new RuntimeException("Institution not found."));
        InstitutionResponse response = modelMapper.map(institution,InstitutionResponse.class);
        List<DepartmentResponse> departmentResponseList = institution.getDepartments()
                .stream()
                .map(department -> DepartmentResponse.builder()
                        .name(department.getName())
                        .build()).toList();

        response.setDepartments(departmentResponseList);
        return response;
    }

    @Override
    public List<InstitutionResponse> getAllInstitutions() {
        return institutionRepository.findAll()
                .stream()
                .map(institution ->{
                    InstitutionResponse response = modelMapper.map(institution, InstitutionResponse.class);
                    List<DepartmentResponse> departmentList = institution.getDepartments()
                            .stream()
                            .map(department ->DepartmentResponse.builder()
                                    .name(department.getName())
                                    .build())
                            .toList();
                    response.setDepartments(departmentList);
                    return response;
                } ).toList();
    }

    @Override
    public InstitutionResponse updateInstitution(Long id, UpdateInstitutionRequest request) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found."));
        modelMapper.map(request, institution);
        Institution updatedInstitution = institutionRepository.save(institution);
        return modelMapper.map(updatedInstitution, InstitutionResponse.class);
    }

    @Override
    public void deleteInstitution(Long id) {
        if (!institutionRepository.existsById(id)) {
            throw new RuntimeException("Institution not found.");
        }
        institutionRepository.deleteById(id);
    }
}
