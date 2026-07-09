package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.LabResponse;

import java.util.List;
public interface LabService {

    LabResponse createLab(CreateLabRequest request);

    LabResponse getLabByCode(String labCode, String institutionCode);

    List<LabResponse> getAllLabs(String institutionCode);

    LabResponse updateLab(UpdateLabRequest request);

    void deleteLab(String labCode, String institutionCode);
}