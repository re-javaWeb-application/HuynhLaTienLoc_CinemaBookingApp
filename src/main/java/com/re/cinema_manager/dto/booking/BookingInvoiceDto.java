package com.re.cinema_manager.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingInvoiceDto {

    private Long bookingId;
    private String bookingCode;
    private String movieTitle;
    private String moviePoster;
    private String cinemaName;
    private String roomName;
    private LocalDateTime showtimeStart;
    private List<String> seatNames;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime bookedAt;
    private String bookingStatus;
    private String customerName;
    private String customerUsername;
}
