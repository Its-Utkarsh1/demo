package com.LabResourceUtilizationPlatform.Controller;


import com.LabResourceUtilizationPlatform.Dtos.Request.CreateBookingRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.BookingResponse;
import com.LabResourceUtilizationPlatform.Entity.Enum.BookingStatus;
import com.LabResourceUtilizationPlatform.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Create Booking
    @PreAuthorize("hasAnyRole('STUDENT','RESEARCHER','RESEARCH_ASSOCIATE','RESEARCH_SCIENTIST','PROFESSOR','ASSOCIATE_PROFESSOR','ASSISTANT_PROFESSOR')")
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request)
            throws BadRequestException {

        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    // Get All Bookings
    @PreAuthorize("hasAnyRole('LAB_MANAGER','DEPARTMENT_HEAD','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<BookingResponse>> getAllBookings(

            @RequestParam(required = false) BookingStatus status,

            @RequestParam(required = false) Long equipmentId,

            @RequestParam(required = false) LocalDate startDate,

            @RequestParam(required = false) LocalDate endDate,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                bookingService.getAllBookings(
                        status,
                        equipmentId,
                        startDate,
                        endDate,
                        pageable));
    }

    // Get My Bookings
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {

        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    // Get Booking By Code
    @GetMapping("/{bookingCode}")
    public ResponseEntity<BookingResponse> getBookingByCode(
            @PathVariable String bookingCode) {

        return ResponseEntity.ok(
                bookingService.getBookingByCode(bookingCode));
    }

    // Update Booking
    @PreAuthorize("hasAnyRole('STUDENT','RESEARCHER','RESEARCH_ASSOCIATE','RESEARCH_SCIENTIST','PROFESSOR','ASSOCIATE_PROFESSOR','ASSISTANT_PROFESSOR')")
    @PutMapping("/{bookingCode}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable String bookingCode,
            @Valid @RequestBody CreateBookingRequest request) {

        return ResponseEntity.ok(
                bookingService.updateBooking(bookingCode, request));
    }

    // Approve Booking
    @PreAuthorize("hasRole('LAB_MANAGER')")
    @PutMapping("/{bookingCode}/approve")
    public ResponseEntity<String> approveBooking(
            @PathVariable String bookingCode) {

        return ResponseEntity.ok(
                bookingService.approveBooking(bookingCode));
    }

    // Cancel Booking
    @PreAuthorize("hasAnyRole('STUDENT','RESEARCHER','RESEARCH_ASSOCIATE','RESEARCH_SCIENTIST','PROFESSOR','ASSOCIATE_PROFESSOR','ASSISTANT_PROFESSOR')")
    @PutMapping("/{bookingCode}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable String bookingCode) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingCode));
    }

    // Calendar View
    @GetMapping("/calendar")
    public ResponseEntity<List<BookingResponse>> getCalendarBookings(

            @RequestParam Integer month,

            @RequestParam Integer year,

            @RequestParam(required = false) Long equipmentId,

            @RequestParam(required = false) Long labId) {

        return ResponseEntity.ok(
                bookingService.getCalendarBookings(
                        month,
                        year,
                        equipmentId,
                        labId));
    }
}