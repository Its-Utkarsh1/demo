package com.LabResourceUtilizationPlatform.Repository;

import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
