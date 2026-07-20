package com.LabResourceUtilizationPlatform.Service;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateMaintenanceRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.MaintenanceResponse;

import java.util.List;

public interface MaintenanceService {

    MaintenanceResponse createMaintenance(CreateMaintenanceRequest request);

    List<MaintenanceResponse> getAllMaintenance();

    MaintenanceResponse getMaintenance(Long id);

    MaintenanceResponse updateMaintenance(
            Long id,
            CreateMaintenanceRequest request
    );
    List<MaintenanceResponse> getMyMaintenance();
    String startMaintenance(Long id);

    String completeMaintenance(Long id);
}
