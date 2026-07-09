package com.LabResourceUtilizationPlatform.Service.ServiceImpl;
import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.DepartmentResponse;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(InstitutionServiceImpl.class);

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
        InstitutionResponse response = modelMapper.map(institutionRepository.save(institution),InstitutionResponse.class);
        logger.info("Institution created: {}", institution.getCode());
        return response;
    }

    @Override
    @Cacheable(value = "institutions", key = "#institutionCode")
    public InstitutionResponse getInstitutionByCode(String institutionCode) {
        Institution institution = institutionRepository.findByCode(institutionCode).orElseThrow(() -> new RuntimeException("Institution not found."));
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
    @Transactional
    @CacheEvict(value = "institutions", key = "#request.code")
    public InstitutionResponse updateInstitution(UpdateInstitutionRequest request) {

        Institution institution = institutionRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        if (!institution.getEmail().equals(request.getEmail())
                && institutionRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Institution email already exists.");
        }

        if (!institution.getPhoneNumber().equals(request.getPhoneNumber())
                && institutionRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Institution phone number already exists.");
        }

        modelMapper.map(request, institution);
        Institution updatedInstitution = institutionRepository.save(institution);
        logger.info("Institution updated: {}", updatedInstitution.getCode());
        return modelMapper.map(updatedInstitution, InstitutionResponse.class);
    }

    @Override
    @CacheEvict(value = "institutions", key = "#institutionCode")
    public void deleteInstitution(String institutionCode) {
        Institution institution = institutionRepository.findByCode(institutionCode).orElseThrow(() -> new RuntimeException("Institution not found."));
        institutionRepository.delete(institution);
        logger.info("Institution deleted: {}", institution.getCode());
    }
}
