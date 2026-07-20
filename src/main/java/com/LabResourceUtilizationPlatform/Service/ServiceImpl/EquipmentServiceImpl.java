package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateEquipmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateEquipmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.EquipmentDetailResponse;
import com.LabResourceUtilizationPlatform.Dtos.Response.EquipmentResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import com.LabResourceUtilizationPlatform.Entity.Equipment;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import com.LabResourceUtilizationPlatform.Repository.BookingRepository;
import com.LabResourceUtilizationPlatform.Repository.DepartmentRepository;
import com.LabResourceUtilizationPlatform.Repository.EquipmentRepository;
import com.LabResourceUtilizationPlatform.Repository.LabRepository;
import com.LabResourceUtilizationPlatform.Service.EquipmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final LabRepository labRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(EquipmentServiceImpl.class);

    @Override
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {

        Lab lab = labRepository
                .findByLabCodeAndInstitution_Code(
                        request.getLabCode(),
                        request.getInstitutionCode())
                .orElseThrow(() ->
                        new RuntimeException("Lab not found."));

        if (equipmentRepository.existsByEquipmentCodeAndLab(
                request.getEquipmentCode(), lab)) {
            throw new RuntimeException("Equipment code already exists.");
        }

        if (equipmentRepository.existsByEquipmentNameAndLab(
                request.getEquipmentName(), lab)) {
            throw new RuntimeException("Equipment name already exists.");
        }

        Equipment equipment = Equipment.builder()
                .equipmentName(request.getEquipmentName())
                .equipmentCode(request.getEquipmentCode())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .description(request.getDescription())
                .specifications(request.getSpecifications())
                .imageUrl(request.getImageUrl())
                .purchaseDate(request.getPurchaseDate())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .status(request.getStatus())
                .lab(lab)
                .build();

        Equipment savedEquipment = equipmentRepository.save(equipment);

        logger.info("Equipment created: {}", savedEquipment.getEquipmentCode());

        return mapToResponse(savedEquipment);
    }
    @Override
    @Transactional
    public Map<String, Long> getEquipmentStatusCounts(String institutionCode) {

        List<Equipment> equipments =
                equipmentRepository.findByLab_Institution_Code(institutionCode);

        Map<String, Long> counts = new HashMap<>();

        counts.put(
                "AVAILABLE",
                equipments.stream()
                        .filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE)
                        .count());

        counts.put(
                "IN_USE",
                equipments.stream()
                        .filter(e -> e.getStatus() == EquipmentStatus.IN_USE)
                        .count());

        counts.put(
                "MAINTENANCE",
                equipments.stream()
                        .filter(e -> e.getStatus() == EquipmentStatus.UNDER_MAINTENANCE)
                        .count());

        counts.put(
                "OUT_OF_SERVICE",
                equipments.stream()
                        .filter(e -> e.getStatus() == EquipmentStatus.OUT_OF_SERVICE)
                        .count());

        return counts;
    }

    //For Lab Manager Only
    @Override
    public EquipmentDetailResponse getEquipmentDetail(String equipmentCode, String labCode, String institutionCode) {
        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
                        equipmentCode,
                        labCode,
                        institutionCode)
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        return mapToDetailResponse(equipment);
    }

    @Override
    @Cacheable(value = "equipment", key = "#institutionCode + ':' + #labCode + ':' + #equipmentCode")
    public EquipmentResponse getEquipmentByCode(String equipmentCode, String labCode, String institutionCode) {
        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
                        equipmentCode,
                        labCode,
                        institutionCode)
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        return mapToResponse(equipment);
    }

    @Override
    public List<EquipmentResponse> getAllEquipment(String labCode, String institutionCode) {
        return equipmentRepository
                .findByLab_LabCodeAndLab_Institution_Code(
                        labCode,
                        institutionCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<EquipmentResponse> getDepartmentEquipment(
            String institutionCode,
            String departmentName) {

        List<Equipment> equipments =
                equipmentRepository.findByLab_Department_NameAndLab_Department_Institution_Code(
                        departmentName,
                        institutionCode);

        return equipments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "equipment", allEntries = true)
    public EquipmentResponse updateEquipment(UpdateEquipmentRequest request) {

        Lab lab = labRepository
                .findByLabCodeAndInstitution_Code(
                        request.getLabCode(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Lab not found."));

        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab(
                        request.getEquipmentCode(),
                        lab)
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        if (request.getNewEquipmentCode() != null
                && !request.getNewEquipmentCode().isBlank()) {

            if (!equipment.getEquipmentCode().equals(request.getNewEquipmentCode())
                    && equipmentRepository.existsByEquipmentCodeAndLab(
                    request.getNewEquipmentCode(), lab)) {

                throw new RuntimeException("Equipment code already exists.");
            }

            equipment.setEquipmentCode(request.getNewEquipmentCode());
        }

        if (!equipment.getEquipmentName().equals(request.getEquipmentName())
                && equipmentRepository.existsByEquipmentNameAndLab(
                request.getEquipmentName(), lab)) {

            throw new RuntimeException("Equipment name already exists.");
        }

        equipment.setEquipmentName(request.getEquipmentName());
        equipment.setManufacturer(request.getManufacturer());
        equipment.setModel(request.getModel());
        equipment.setDescription(request.getDescription());
        equipment.setSpecifications(request.getSpecifications());
        equipment.setImageUrl(request.getImageUrl());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setPrice(request.getPrice());
        equipment.setQuantity(request.getQuantity());
        equipment.setStatus(request.getStatus());

        Equipment updatedEquipment = equipmentRepository.save(equipment);
        logger.info("Equipment updated: {}", updatedEquipment.getEquipmentCode());
        return mapToResponse(updatedEquipment);
    }

    @Override
    @CacheEvict(value = "equipment", allEntries = true)
    public void deleteEquipment(String equipmentCode, String labCode, String institutionCode) {

        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
                        equipmentCode,
                        labCode,
                        institutionCode)
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        equipmentRepository.delete(equipment);
        logger.info("Equipment deleted: {}", equipment.getEquipmentCode());
    }

    private EquipmentResponse mapToResponse(Equipment equipment) {

        Integer bookedQuantity = bookingRepository.getBookedQuantity(
                equipment.getId(),
                List.of(
                        BookingStatus.PENDING,
                        BookingStatus.APPROVED
                )
        );

        return EquipmentResponse.builder()
                .equipmentName(equipment.getEquipmentName())
                .equipmentCode(equipment.getEquipmentCode())
                .model(equipment.getModel())
                .description(equipment.getDescription())
                .specifications(equipment.getSpecifications())
                .imageUrl(equipment.getImageUrl())
                .labCode(equipment.getLab().getLabCode())
                .availableQuantity(
                        Math.max(0, equipment.getQuantity() - bookedQuantity)
                )
                .status(equipment.getStatus().name())
                .lab(equipment.getLab().getLabName())
                .department(equipment.getLab().getDepartment().getName())
                .institution(equipment.getLab().getInstitution().getName())
                .build();

    }

    private EquipmentDetailResponse mapToDetailResponse(Equipment equipment) {

        Integer bookedQuantity = bookingRepository.getBookedQuantity(
                equipment.getId(),
                List.of(
                        BookingStatus.PENDING,
                        BookingStatus.APPROVED
                )
        );

        return EquipmentDetailResponse.builder()
                .equipmentName(equipment.getEquipmentName())
                .equipmentCode(equipment.getEquipmentCode())
                .manufacturer(equipment.getManufacturer())
                .model(equipment.getModel())
                .description(equipment.getDescription())
                .specifications(equipment.getSpecifications())
                .imageUrl(equipment.getImageUrl())

                .purchaseDate(equipment.getPurchaseDate())
                .price(equipment.getPrice())
                .quantity(equipment.getQuantity())
                .availableQuantity(
                        Math.max(0, equipment.getQuantity() - bookedQuantity)
                )
                .status(equipment.getStatus().name())
                .lab(equipment.getLab().getLabName())
                .department(equipment.getLab().getDepartment().getName())
                .institution(equipment.getLab().getInstitution().getName())
                .build();
    }
}
