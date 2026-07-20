package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Enum.MaintenanceStatus;
import com.LabResourceUtilizationPlatform.Entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByStatus(MaintenanceStatus status);

    List<Maintenance> findByScheduledDate(LocalDate scheduledDate);

    List<Maintenance> findByEquipment_Id(Long equipmentId);

    List<Maintenance> findByTechnician_Id(Long technicianId);

    List<Maintenance> findByEquipment_Lab_Id(Long labId);

    List<Maintenance> findByEquipment_Lab_LabManager_Id(Long labManagerId);

    List<Maintenance> findByEquipment_Lab_LabManager_IdAndStatus(
            Long labManagerId,
            MaintenanceStatus status
    );
    List<Maintenance> findByTechnician_Email(String email);

    long countByStatus(MaintenanceStatus status);

    long countByEquipment_Lab_LabManager_IdAndStatus(
            Long labManagerId,
            MaintenanceStatus status
    );
}