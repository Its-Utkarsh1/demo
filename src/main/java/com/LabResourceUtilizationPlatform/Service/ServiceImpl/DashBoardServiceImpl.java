package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Response.TechnicianDashboardResponse;
import com.LabResourceUtilizationPlatform.Dtos.Response.WeeklyUtilizationResponse;
import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import com.LabResourceUtilizationPlatform.Entity.Enum.RoleName;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.BookingRepository;
import com.LabResourceUtilizationPlatform.Repository.EquipmentRepository;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    public WeeklyUtilizationResponse getWeeklyUtilization() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleName role = user.getRole().getRoleName();

        switch (role) {

            case STUDENT:
                return getStudentWeeklyUtilization(user);

            case RESEARCHER:
            case RESEARCH_ASSOCIATE:
            case RESEARCH_SCIENTIST:
                return getResearcherWeeklyUtilization(user);

            case PROFESSOR:
            case ASSOCIATE_PROFESSOR:
            case ASSISTANT_PROFESSOR:
                return getFacultyWeeklyUtilization(user);

            case LAB_TECHNICIAN:
                throw new RuntimeException(
                        "Weekly utilization is not available for Lab Technician."
                );

            case LAB_MANAGER:
                return getLabManagerWeeklyUtilization(user);

            case DEPARTMENT_HEAD:
                return getDepartmentHeadWeeklyUtilization(user);

            case INSTITUTION_ADMIN:
                return getInstitutionAdminWeeklyUtilization(user);

            case SYSTEM_ADMIN:
                return getSystemAdminWeeklyUtilization();

            default:
                throw new RuntimeException("Role not supported");
        }
    }

    private WeeklyUtilizationResponse getStudentWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23, 59, 59);

        List<Booking> bookings =
                bookingRepository.findByUserIdAndStartTimeBetween(
                        user.getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse getResearcherWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23, 59, 59);

        List<Booking> bookings =
                bookingRepository.findByUserIdAndStartTimeBetween(
                        user.getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse getFacultyWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23, 59, 59);

        List<Booking> bookings =
                bookingRepository.findFacultyBookings(
                        user.getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    public TechnicianDashboardResponse getTechnicianDashboard(User user) {

        Long departmentId = user.getDepartment().getId();

        return TechnicianDashboardResponse.builder()
                .availableEquipment(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.AVAILABLE
                        )
                )
                .inUseEquipment(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.IN_USE
                        )
                )
                .underMaintenance(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.UNDER_MAINTENANCE
                        )
                )
                .outOfService(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.OUT_OF_SERVICE
                        )
                )
                .build();
    }

    private WeeklyUtilizationResponse getLabManagerWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23,59,59);

        List<Booking> bookings =
                bookingRepository.findDepartmentBookings(
                        user.getDepartment().getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse getDepartmentHeadWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23,59,59);

        List<Booking> bookings =
                bookingRepository.findDepartmentBookings(
                        user.getDepartment().getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse getInstitutionAdminWeeklyUtilization(User user) {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23,59,59);

        List<Booking> bookings =
                bookingRepository.findInstitutionBookings(
                        user.getInstitution().getId(),
                        weekStart,
                        weekEnd
                );

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse getSystemAdminWeeklyUtilization() {

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        LocalDateTime weekStart = monday.atStartOfDay();

        LocalDateTime weekEnd = monday.plusDays(6).atTime(23,59,59);

        List<Booking> bookings = bookingRepository.findAll()
                .stream()
                .filter(b -> !b.getStartTime().isBefore(weekStart))
                .filter(b -> !b.getStartTime().isAfter(weekEnd))
                .toList();

        return calculateWeeklyHours(bookings);
    }

    private WeeklyUtilizationResponse calculateWeeklyHours(List<Booking> bookings) {

        List<Integer> weekly =
                new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));

        for (Booking booking : bookings) {

            int dayIndex =
                    booking.getStartTime()
                            .getDayOfWeek()
                            .getValue() - 1;

            long hours = Duration.between(
                    booking.getStartTime(),
                    booking.getEndTime()
            ).toHours();

            weekly.set(dayIndex, weekly.get(dayIndex) + (int) hours);
        }

        return new WeeklyUtilizationResponse(weekly);
    }

    @Override
    public TechnicianDashboardResponse getTechnicianDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long departmentId = user.getDepartment().getId();

        return TechnicianDashboardResponse.builder()
                .availableEquipment(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.AVAILABLE
                        )
                )
                .inUseEquipment(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.IN_USE
                        )
                )
                .underMaintenance(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.UNDER_MAINTENANCE
                        )
                )
                .outOfService(
                        equipmentRepository.countByLab_Department_IdAndStatus(
                                departmentId,
                                EquipmentStatus.OUT_OF_SERVICE
                        )
                )
                .build();
    }

}
