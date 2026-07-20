package com.LabResourceUtilizationPlatform.Service.ServiceImpl;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateBookingRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.BookingResponse;
import com.LabResourceUtilizationPlatform.Entity.Booking;
import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import com.LabResourceUtilizationPlatform.Entity.Equipment;
import com.LabResourceUtilizationPlatform.Entity.Lab;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Repository.BookingRepository;
import com.LabResourceUtilizationPlatform.Repository.EquipmentRepository;
import com.LabResourceUtilizationPlatform.Repository.LabRepository;
import com.LabResourceUtilizationPlatform.Repository.UserRepository;
import com.LabResourceUtilizationPlatform.Service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.UUID;
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
    private final LabRepository labRepository;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) throws BadRequestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
                        request.getEquipmentCode(),
                        request.getLabCode(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        // Validate booking time
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time.");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking cannot be created for a past time.");
        }

        // Calculate already booked quantity for the requested time slot
        Integer bookedQuantity = bookingRepository.getBookedQuantityForTimeSlot(
                equipment.getId(),
                List.of(
                        BookingStatus.PENDING,
                        BookingStatus.APPROVED
                ),
                request.getStartTime(),
                request.getEndTime()
        );

        if (bookedQuantity == null) {
            bookedQuantity = 0;
        }

        int availableQuantity = equipment.getQuantity() - bookedQuantity;

        if (availableQuantity <= 0) {
            throw new BadRequestException("Equipment is currently unavailable.");
        }

        if (request.getQuantity() > availableQuantity) {
            throw new BadRequestException(
                    "Only " + availableQuantity + " unit(s) are available."
            );
        }

        // Generate unique booking code
        String bookingCode = "BK-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .user(user)
                .equipment(equipment)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .quantity(request.getQuantity())
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public List<BookingResponse> getPendingBookingsForLabManager() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Booking> bookings =
                bookingRepository.findByEquipment_Lab_LabManager_IdAndStatus(
                        manager.getId(),
                        BookingStatus.PENDING
                );

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
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

        Equipment equipment = equipmentRepository
                .findByEquipmentCodeAndLab_LabCodeAndLab_Institution_Code(
                        request.getEquipmentCode(),
                        request.getLabCode(),
                        request.getInstitutionCode())
                .orElseThrow(() -> new RuntimeException("Equipment not found."));

        Integer bookedQuantity =
                bookingRepository.getBookedQuantityForTimeSlotExcludingBooking(
                        equipment.getId(),
                        List.of(
                                BookingStatus.PENDING,
                                BookingStatus.APPROVED
                        ),
                        request.getStartTime(),
                        request.getEndTime(),
                        booking.getId()
                );

        int availableQuantity = equipment.getQuantity() - bookedQuantity;

        if (availableQuantity <= 0) {
            throw new RuntimeException("Equipment is currently unavailable.");
        }

        if (request.getQuantity() > availableQuantity) {
            throw new RuntimeException(
                    "Only " + availableQuantity + " unit(s) are available."
            );
        }

        booking.setEquipment(equipment);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPurpose(request.getPurpose());
        booking.setQuantity(request.getQuantity());

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

        Integer bookedQuantity =
                bookingRepository.getBookedQuantityForTimeSlotExcludingBooking(
                        booking.getEquipment().getId(),
                        List.of(
                                BookingStatus.PENDING,
                                BookingStatus.APPROVED
                        ),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        booking.getId()
                );

        int available =
                booking.getEquipment().getQuantity() - bookedQuantity;

        if (booking.getQuantity() > available) {
            throw new RuntimeException(
                    "Only " + available + " unit(s) are available."
            );
        }

        booking.setStatus(BookingStatus.APPROVED);

        bookingRepository.save(booking);

        return "Booking approved successfully.";
    }

    @Override
    @Transactional
    public String cancelBooking(String bookingCode) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can cancel only your own bookings.");
        }

        cancel(booking);

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

    @Transactional
    private void cancel(Booking booking) {

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Completed bookings cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public String cancelBookingByManager(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        // No ownership check; authorization is handled by the role.
        cancel(booking);

        return "Booking cancelled by Lab Manager successfully.";
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
        response.setQuantity(booking.getQuantity());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());

        return response;
    }
}
