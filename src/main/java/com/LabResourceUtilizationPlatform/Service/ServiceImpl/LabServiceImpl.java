package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateLabRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.LabResponse;
import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import com.LabResourceUtilizationPlatform.Repository.DepartmentRepository;
import com.LabResourceUtilizationPlatform.Repository.InstitutionRepository;
import com.LabResourceUtilizationPlatform.Repository.LabRepository;
import com.LabResourceUtilizationPlatform.Service.LabService;
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
public class LabServiceImpl implements LabService {

    private final LabRepository labRepository;
    private final ModelMapper modelMapper;
    private final InstitutionRepository institutionRepository;
    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(LabServiceImpl.class);

    @Override
    public LabResponse createLab(CreateLabRequest request) {

        Institution institution = institutionRepository.findByCode(request.getInstitutionCode()).orElseThrow(() -> new RuntimeException("Institution not found."));
        Department department = departmentRepository.findByNameAndInstitution_Code(request.getDepartmentName(), institution.getCode()).orElseThrow(() -> new RuntimeException("Department not found."));

        if (labRepository.existsByLabCodeAndInstitutionAndDepartment(request.getLabCode(),institution,department)) {
            throw new RuntimeException("Lab code already exists in this department.");
        }

        if (labRepository.existsByLabNameAndInstitutionAndDepartment(request.getLabName(),institution,department)) {
            throw new RuntimeException("Lab name already exists in this department.");
        }

        Lab lab = Lab.builder()
                .labName(request.getLabName())
                .labCode(request.getLabCode())
                .location(request.getLocation())
                .usercapacity(request.getUserCapacity())
                .status(request.getStatus())
                .institution(institution)
                .department(department)
                .build();

        Lab savedLab = labRepository.save(lab);
        logger.info("Lab created successfully: {}", savedLab.getLabName());

       return  mapToResponse(savedLab);
    }

    @Override
    public List<LabResponse> getLabsByDepartment(
            String institutionCode,
            String departmentName) {

        List<Lab> labs = labRepository
                .findByInstitution_CodeAndDepartment_Name(
                        institutionCode,
                        departmentName);

        return labs.stream()
                .map(lab -> modelMapper.map(lab, LabResponse.class))
                .toList();
    }

    @Override
    @Cacheable(value = "labs", key = "#institutionCode + ':' + #labCode")
    public LabResponse getLabByCode(String labCode, String institutionCode) {
        Lab lab = labRepository
                .findByLabCodeAndInstitution_Code(labCode, institutionCode)
                .orElseThrow(() -> new RuntimeException("Lab not found."));

        return mapToResponse(lab);
    }

    @Override
    public List<LabResponse> getAllLabs(String institutionCode) {
        return labRepository.findByInstitution_Code(institutionCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "labs", key = "#request.institutionCode + ':' + #request.labCode")
    public LabResponse updateLab(UpdateLabRequest request) {

        Lab lab = labRepository
                .findByLabCodeAndInstitution_Code(
                        request.getLabCode(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Lab not found."));

        Institution institution = institutionRepository
                .findByCode(request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Institution not found."));

        Department department = departmentRepository
                .findByNameAndInstitution_Code(
                        request.getDepartmentName(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Department not found."));

        // Update lab code if provided
        if (request.getNewLabCode() != null && !request.getNewLabCode().isBlank()) {

            if (!lab.getLabCode().equals(request.getNewLabCode())
                    && labRepository.existsByLabCodeAndInstitutionAndDepartment(
                    request.getNewLabCode(),
                    institution,
                    department)) {

                throw new RuntimeException("Lab code already exists.");
            }

            lab.setLabCode(request.getNewLabCode());
        }

        // Validate lab name
        if (!lab.getLabName().equals(request.getLabName())
                && labRepository.existsByLabNameAndInstitutionAndDepartment(
                request.getLabName(),
                institution,
                department)) {

            throw new RuntimeException("Lab name already exists.");
        }

        lab.setLabName(request.getLabName());
        lab.setLocation(request.getLocation());
        lab.setUsercapacity(request.getUserCapacity());
        lab.setStatus(request.getStatus());
        lab.setInstitution(institution);
        lab.setDepartment(department);

        Lab updatedLab = labRepository.save(lab);

        logger.info("Lab updated successfully: {}", updatedLab.getLabName());

        return mapToResponse(updatedLab);
    }

    @Override
    @CacheEvict(
            value = "labs",
            key = "#institutionCode + ':' + #labCode"
    )
    public void deleteLab(String labCode, String institutionCode) {
        Lab lab = labRepository
                .findByLabCodeAndInstitution_Code(
                        labCode,
                        institutionCode)
                .orElseThrow(() -> new RuntimeException("Lab not found."));

        labRepository.delete(lab);

        logger.info("Lab deleted successfully: {}", lab.getLabName());
    }

    private LabResponse mapToResponse(Lab lab) {

        LabResponse response = modelMapper.map(lab, LabResponse.class);
        response.setInstitution(lab.getInstitution().getName());
        response.setDepartment(lab.getDepartment().getName());
        response.setStatus(lab.getStatus().name());

        return response;
    }

}
