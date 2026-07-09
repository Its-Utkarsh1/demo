package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution,Long> {
    Optional<Institution> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
