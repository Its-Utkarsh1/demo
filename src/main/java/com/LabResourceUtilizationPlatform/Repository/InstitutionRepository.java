package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution,Long> {
    Optional<Institution> findByCode(String code);

    Optional<Institution> findByName(String name);

    Optional<Institution> findByEmail(String email);

    Optional<Institution> findByPhoneNumber(String phoneNumber);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
