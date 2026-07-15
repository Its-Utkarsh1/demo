package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Dtos.Response.UserResponse;
import com.LabResourceUtilizationPlatform.Entity.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);
    List<User> findByInstitution_Code(String institutionCode);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
