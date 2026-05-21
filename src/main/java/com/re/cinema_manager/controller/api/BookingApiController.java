package com.re.cinema_manager.controller.api;

import com.re.cinema_manager.dto.booking.BookingHistoryItemDto;
import com.re.cinema_manager.dto.booking.BookingInvoiceDto;
import com.re.cinema_manager.dto.booking.CreateBookingRequest;
import com.re.cinema_manager.dto.booking.SeatSelectionView;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.BookingHistoryService;
import com.re.cinema_manager.service.BookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingApiController {

    private final BookingService bookingService;
    private final BookingHistoryService bookingHistoryService;

    @GetMapping("/showtimes/{showtimeId}/seats")
    public SeatSelectionView getSeats(@PathVariable Long showtimeId) {
        return bookingService.getSeatSelection(showtimeId);
    }

    @PostMapping
    public ResponseEntity<BookingInvoiceDto> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            HttpSession session) {
        User user = requireCustomer(session);
        BookingInvoiceDto invoice = bookingService.createBooking(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @GetMapping("/history")
    public Page<BookingHistoryItemDto> history(
            HttpSession session,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = requireCustomer(session);
        return bookingHistoryService.getBookingHistory(user.getId(), pageable);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, HttpSession session) {
        User user = requireCustomer(session);
        bookingService.cancelBooking(user.getId(), bookingId);
        return ResponseEntity.noContent().build();
    }

    private static User requireCustomer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalStateException("API đặt vé chỉ dành cho khách hàng (CUSTOMER)");
        }
        return user;
    }
}
