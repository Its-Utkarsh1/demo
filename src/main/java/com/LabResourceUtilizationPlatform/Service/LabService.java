package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.LabResponse;

import java.util.List;

public interface LabService {

    LabResponse createLab(CreateLabRequest request);

    LabResponse getLabById(Long id);

    List<LabResponse> getAllLabs();

    LabResponse updateLab(Long id, CreateLabRequest request);

    void deleteLab(Long id);
}
