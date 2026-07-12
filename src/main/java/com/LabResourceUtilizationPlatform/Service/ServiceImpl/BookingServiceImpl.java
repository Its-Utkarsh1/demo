package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateBookingRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.BookingResponse;
import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import com.LabResourceUtilizationPlatform.Entity.Equipment;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.BookingRepository;
import com.LabResourceUtilizationPlatform.Repository.EquipmentRepository;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) throws BadRequestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found."));


        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time.");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking cannot be created for a past time.");
        }

        boolean exists = bookingRepository
                .existsByEquipmentIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        equipment.getId(),
                        request.getEndTime(),
                        request.getStartTime());

        if (exists) {
            throw new BadRequestException("Equipment is already booked for the selected time.");
        }

        Booking booking = Booking.builder()
                .user(user)
                .equipment(equipment)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);

        booking.setBookingCode("BK-" + String.format("%06d", booking.getId()));

        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public Page<BookingResponse> getAllBookings(
            BookingStatus status,
            Long equipmentId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        Page<Booking> bookings = bookingRepository.findAll(pageable);

        return bookings.map(booking -> mapToResponse(booking));
    }

    @Override
    @Transactional
    public List<BookingResponse> getMyBookings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(String bookingCode, CreateBookingRequest request) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be updated.");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("Start time must be before end time.");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Booking cannot be updated to a past time.");
        }

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        boolean exists = bookingRepository
                .existsByEquipmentIdAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                        equipment.getId(),
                        request.getEndTime(),
                        request.getStartTime(),
                        booking.getId());

        if (exists) {
            throw new RuntimeException("Equipment is already booked for the selected time.");
        }

        booking.setEquipment(equipment);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPurpose(request.getPurpose());

        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public String approveBooking(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be approved.");
        }

        booking.setStatus(BookingStatus.APPROVED);

        bookingRepository.save(booking);

        return "Booking approved successfully.";
    }

    @Override
    @Transactional
    public String cancelBooking(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Completed bookings cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        return "Booking cancelled successfully.";
    }


    @Override
    @Transactional
    public List<BookingResponse> getCalendarBookings(
            Integer month,
            Integer year,
            Long equipmentId,
            Long labId) {

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = lastDay.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(start, end);

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookingResponse mapToResponse(Booking booking) {

        BookingResponse response = modelMapper.map(booking, BookingResponse.class);

        response.setBookingCode(booking.getBookingCode());
        response.setBookedBy(booking.getUser().getFullName());
        response.setEquipmentName(booking.getEquipment().getEquipmentName());
        response.setInstitutionName(booking.getUser().getInstitution().getName());
        response.setDepartmentName(booking.getUser().getDepartment().getName());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setPurpose(booking.getPurpose());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());

        return response;
    }
}
