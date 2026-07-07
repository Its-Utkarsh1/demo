package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;

import java.util.List;

public interface InstitutionService {
    InstitutionResponse createInstitution(CreateInstitutionRequest request);

    InstitutionResponse getInstitutionById(Long id);

    List<InstitutionResponse> getAllInstitutions();

    InstitutionResponse updateInstitution(Long id, UpdateInstitutionRequest request);

    void deleteInstitution(Long id);
}
