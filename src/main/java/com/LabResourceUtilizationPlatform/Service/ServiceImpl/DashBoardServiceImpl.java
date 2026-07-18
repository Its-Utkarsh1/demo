package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Response.*;
import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import com.LabResourceUtilizationPlatform.Entity.Enum.RoleName;
import com.LabResourceUtilizationPlatform.Entity.Institution;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.*;
import com.LabResourceUtilizationPlatform.Service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final LabRepository labRepository;
    private final DepartmentRepository departmentRepository;
    private final InstitutionRepository institutionRepository;
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

    @Override
    public StudentDashboardResponse getStudentDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return StudentDashboardResponse.builder()
                .totalBookings(
                        bookingRepository.countByUserId(user.getId())
                )
                .upcomingBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.APPROVED
                        )
                )
                .completedBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.COMPLETED
                        )
                )
                .cancelledBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.CANCELLED
                        )
                )
                .build();
    }

    @Override
    public ResearcherDashboardResponse getResearcherDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResearcherDashboardResponse.builder()
                .totalBookings(
                        bookingRepository.countByUserId(user.getId())
                )
                .activeBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.APPROVED
                        )
                )
                .completedBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.COMPLETED
                        )
                )
                .availableEquipment(
                        equipmentRepository.countByStatus(
                                EquipmentStatus.AVAILABLE
                        )
                )
                .build();
    }

    @Override
    public FacultyDashboardResponse getFacultyDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings =
                bookingRepository.findTop5UserBookings(
                        user.getId(),
                        PageRequest.of(0, 5)
                );

        List<RecentBookingResponse> recentBookings =
                bookings.stream()
                        .map(booking ->
                                RecentBookingResponse.builder()
                                        .equipmentName(
                                                booking.getEquipment().getEquipmentName())
                                        .bookedBy(
                                                booking.getUser().getFullName())
                                        .status(
                                                booking.getStatus())
                                        .startTime(
                                                booking.getStartTime())
                                        .endTime(
                                                booking.getEndTime())
                                        .build()
                        )
                        .toList();

        return FacultyDashboardResponse.builder()
                .totalBookings(
                        bookingRepository.countByUserId(user.getId())
                )
                .pendingApprovals(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.PENDING
                        )
                )
                .completedBookings(
                        bookingRepository.countByUserIdAndStatus(
                                user.getId(),
                                BookingStatus.COMPLETED
                        )
                )
                .departmentEquipment(
                        equipmentRepository.countByLab_Department_Id(
                                user.getDepartment().getId()
                        )
                )
                .recentBookings(recentBookings)
                .build();
    }

    @Override
    public LabManagerDashboardResponse getLabManagerDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lab lab = labRepository.findByLabManagerId(user.getId())
                .orElseThrow(() -> new RuntimeException("No lab assigned to this manager"));

        Long labId = lab.getId();

        long totalEquipment = equipmentRepository.countByLabId(labId);

        long availableEquipment =
                equipmentRepository.countByLabIdAndStatus(
                        labId,
                        EquipmentStatus.AVAILABLE
                );

        long maintenanceEquipment =
                equipmentRepository.countByLabIdAndStatus(
                        labId,
                        EquipmentStatus.UNDER_MAINTENANCE
                );

        long outOfServiceEquipment =
                equipmentRepository.countByLabIdAndStatus(
                        labId,
                        EquipmentStatus.OUT_OF_SERVICE
                );

        long bookedEquipment =
                totalEquipment
                        - availableEquipment
                        - maintenanceEquipment
                        - outOfServiceEquipment;

        long pendingBookings =
                bookingRepository.countByEquipment_Lab_IdAndStatus(
                        labId,
                        BookingStatus.PENDING
                );

        long rejectedBookings =
                bookingRepository.countByEquipment_Lab_IdAndStatus(
                        labId,
                        BookingStatus.REJECTED
                );

        long approvedToday =
                bookingRepository.countApprovedToday(
                        labId,
                        BookingStatus.APPROVED,
                        LocalDate.now()
                );

        double utilizationRate =
                totalEquipment == 0
                        ? 0
                        : (bookedEquipment * 100.0) / totalEquipment;

        return LabManagerDashboardResponse.builder()
                .totalEquipment(totalEquipment)
                .equipmentInUse(bookedEquipment)
                .maintenanceDue(maintenanceEquipment)
                .utilizationRate(utilizationRate)

                .availableEquipment(availableEquipment)
                .bookedEquipment(bookedEquipment)
                .maintenanceEquipment(maintenanceEquipment)
                .outOfServiceEquipment(outOfServiceEquipment)

                .pendingBookings(pendingBookings)
                .approvedToday(approvedToday)
                .rejectedBookings(rejectedBookings)

                .todayMaintenance(List.of())
                .build();
    }

    @Override
    public DepartmentHeadDashboardResponse getDepartmentHeadDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long departmentId = user.getDepartment().getId();

        List<Booking> bookings =
                bookingRepository.findTop5DepartmentBookings(
                        departmentId,
                        PageRequest.of(0,5)
                );

        List<RecentBookingResponse> recentBookings =
                bookings.stream()
                        .map(booking ->

                                RecentBookingResponse.builder()
                                        .equipmentName(
                                                booking.getEquipment().getEquipmentName())
                                        .bookedBy(
                                                booking.getUser().getFullName())
                                        .status(
                                                booking.getStatus())
                                        .startTime(
                                                booking.getStartTime())
                                        .endTime(
                                                booking.getEndTime())
                                        .build()

                        ).toList();

        return DepartmentHeadDashboardResponse.builder()
                .totalLabs(
                        labRepository.countByDepartmentId(departmentId))
                .totalEquipment(
                        equipmentRepository.countByLab_Department_Id(departmentId))
                .activeBookings(
                        bookingRepository.countByEquipment_Lab_Department_Id(departmentId))
                .departmentUsers(
                        userRepository.countByDepartmentId(departmentId))
                .recentBookings(recentBookings)
                .build();
    }

    @Override
    public InstitutionAdminDashboardResponse getInstitutionAdminDashboard() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long institutionId = user.getInstitution().getId();

        return InstitutionAdminDashboardResponse.builder()
                .totalDepartments(
                        departmentRepository.countByInstitutionId(institutionId)
                )
                .totalLabs(
                        labRepository.countByInstitutionId(institutionId)
                )
                .totalEquipment(
                        equipmentRepository.countByLab_Institution_Id(institutionId)
                )
                .totalUsers(
                        userRepository.countByInstitutionId(institutionId)
                )
                .monthlyBookings(
                        bookingRepository.countByInstitutionId(institutionId)
                )
                .build();
    }

    @Override
    public SystemAdminDashboardResponse getSystemAdminDashboard() {

        List<Institution> institutions =
                institutionRepository.findAllByOrderByIdDesc(
                        PageRequest.of(0, 5)
                );

        List<RecentActivityResponse> recentActivities =
                institutions.stream()
                        .map(institution ->
                                RecentActivityResponse.builder()
                                        .institutionName(institution.getName())
                                        .activity("Institution Registered")
                                        .status("Completed")
                                        .build()
                        )
                        .toList();

        return SystemAdminDashboardResponse.builder()
                .totalInstitutions(institutionRepository.count())
                .totalDepartments(departmentRepository.count())
                .totalLabs(labRepository.count())
                .totalEquipment(equipmentRepository.count())
                .totalUsers(userRepository.count())
                .recentActivities(recentActivities)
                .build();
    }

}
