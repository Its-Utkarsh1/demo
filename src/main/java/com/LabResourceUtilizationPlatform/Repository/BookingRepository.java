package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<Booking> findByBookingCode(String bookingCode);

    boolean existsByBookingCode(String bookingCode);

    boolean existsByEquipmentIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long equipmentId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Booking> findByUser(User user);

    boolean existsByEquipmentIdAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(Long id, @NotNull(message = "End time is required") @Future(message = "End time must be in the future") LocalDateTime endTime, @NotNull(message = "Start time is required") @Future(message = "Start time must be in the future") LocalDateTime startTime, Long id1);

    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByUserIdAndStartTimeBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
SELECT b
FROM Booking b
WHERE b.user.id = :userId
AND b.startTime BETWEEN :start AND :end
""")
    List<Booking> findFacultyBookings(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
SELECT b
FROM Booking b
WHERE b.equipment.lab.department.id = :departmentId
AND b.startTime BETWEEN :start AND :end
""")
    List<Booking> findDepartmentBookings(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
SELECT b
FROM Booking b
WHERE b.equipment.lab.institution.id = :institutionId
AND b.startTime BETWEEN :start AND :end
""")
    List<Booking> findInstitutionBookings(
            @Param("institutionId") Long institutionId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
