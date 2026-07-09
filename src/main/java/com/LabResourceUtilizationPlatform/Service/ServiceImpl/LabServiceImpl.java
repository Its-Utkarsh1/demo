package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateLabRequest;
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
import org.springframework.stereotype.Service;

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
        Department department = departmentRepository.findByNameAndInstitution(request.getDepartmentName(), institution).orElseThrow(() -> new RuntimeException("Department not found."));

        if (labRepository.existsByLabCodeAndInstitutionAndDepartment(request.getLabCode(),institution,department)) {
            throw new RuntimeException("Lab code already exists.");
        }

        if (labRepository.existsByLabNameAndInstitutionAndDepartment(request.getLabName(),institution,department)) {
            throw new RuntimeException("Lab name already exists.");
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

        LabResponse response = modelMapper.map(savedLab, LabResponse.class);
        response.setInstitution(savedLab.getInstitution().getName());
        response.setDepartment(savedLab.getDepartment().getName());
        response.setStatus(savedLab.getStatus().name());
        return response;
    }

    @Override
    public LabResponse getLabById(Long id) {
        return null;
    }

    @Override
    public List<LabResponse> getAllLabs() {
        return List.of();
    }

    @Override
    public LabResponse updateLab(Long id, CreateLabRequest request) {
        return null;
    }

    @Override
    public void deleteLab(Long id) {

    }
}
