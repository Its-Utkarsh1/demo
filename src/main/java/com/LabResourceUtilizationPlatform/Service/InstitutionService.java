package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;

import java.util.List;

public interface InstitutionService {
    InstitutionResponse createInstitution(CreateInstitutionRequest request);

    InstitutionResponse getInstitutionByCode(String institutionCode);

    List<InstitutionResponse> getAllInstitutions();

    InstitutionResponse updateInstitution(UpdateInstitutionRequest request);

    void deleteInstitution(String instituteCode);
}
