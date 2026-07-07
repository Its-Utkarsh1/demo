package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;
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

    private InstitutionRepository institutionRepository;
    private ModelMapper modelMapper;

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
        return modelMapper.map(institution,InstitutionResponse.class);
    }

    @Override
    public List<InstitutionResponse> getAllInstitutions() {
        return institutionRepository.findAll()
                .stream()
                .map(institution -> modelMapper.map(institution,InstitutionResponse.class))
                .toList();
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
