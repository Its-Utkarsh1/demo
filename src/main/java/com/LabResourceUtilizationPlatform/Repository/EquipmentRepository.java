package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByEquipmentCodeAndLab(
            String equipmentCode,
            Lab lab);

    Optional<Equipment> findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
            String equipmentCode,
            String labCode,
            String institutionCode);

    List<Equipment> findByLab_LabCodeAndLab_Institution_Code(
            String labCode,
            String institutionCode);

    boolean existsByEquipmentCodeAndLab(
            String equipmentCode,
            Lab lab);

    boolean existsByEquipmentNameAndLab(
            String equipmentName,
            Lab lab);

    List<Equipment> findByLab_Institution_Code(String institutionCode);
}