package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Response.WeeklyUtilizationResponse;
import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.Enum.RoleName;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.BookingRepository;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.BookingService;
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

import static com.LabResourceUtilizationPlatform.Entity.Enum.RoleName.STUDENT;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

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
                return getTechnicianWeeklyUtilization(user);

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

    private WeeklyUtilizationResponse getTechnicianWeeklyUtilization(User user) {

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

}
