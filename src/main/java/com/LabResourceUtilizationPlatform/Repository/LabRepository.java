package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {

    boolean existsByLabCodeAndInstitutionAndDepartment(
            String labCode,
            Institution institution,
            Department department
    );

    boolean existsByLabNameAndInstitutionAndDepartment(
            String labName,
            Institution institution,
            Department department
    );

    List<Lab> findByInstitution_Code(String institutionCode);

    Optional<Lab> findByLabCodeAndInstitution_Code(String labCode, String institutionCode);
}
