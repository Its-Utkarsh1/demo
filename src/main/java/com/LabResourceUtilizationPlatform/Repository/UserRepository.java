package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Department;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Entity.Role;
import com.LabResourceUtilizationPlatform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // Authentication
    Optional<User> findByEmail(String email);

    // Validation
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByRegistrationId(String registrationId);

    // Search
    Optional<User> findByRegistrationId(String registrationId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole(Role role);

    List<User> findByInstitution(Institution institution);

    List<User> findByDepartment(Department department);

    List<User> findByRoleAndInstitution(Role role, Institution institution);

    List<User> findByRoleAndDepartment(Role role, Department department);

    // Search by Name
    List<User> findByFullNameContainingIgnoreCase(String fullName);
}
