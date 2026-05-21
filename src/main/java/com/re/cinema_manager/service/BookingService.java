package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.booking.BookingInvoiceDto;
import com.re.cinema_manager.dto.booking.CreateBookingRequest;
import com.re.cinema_manager.dto.booking.SeatSelectionView;

public interface BookingService {

    String PAYMENT_AT_COUNTER = "AT_COUNTER";

    SeatSelectionView getSeatSelection(Long showtimeId);

    BookingInvoiceDto createBooking(Long userId, CreateBookingRequest request);

    /** Hủy vé: xóa tickets (giải phóng ghế), CANCELLED, payment → FAILED nếu có. */
    void cancelBooking(Long userId, Long bookingId);

    BookingInvoiceDto getInvoiceByIdAndUser(Long bookingId, Long userId);

    BookingInvoiceDto getInvoiceById(Long bookingId);
}
