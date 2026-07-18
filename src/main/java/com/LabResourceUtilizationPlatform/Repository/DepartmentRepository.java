package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {

    Optional<Department> findByNameAndInstitution_Code(String name, String institutionCode);

    List<Department> findByInstitution_Code(String institutionCode);

    boolean existsByNameAndInstitution_Code(String name, String institutionCode);

    Long countByInstitutionId(Long institutionId);
}