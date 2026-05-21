package com.re.cinema_manager.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingHistoryItemDto {

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
    private LocalDateTime bookedAt;
    /** PAID | PENDING | CANCELLED */
    private String bookingStatus;
    /** Có thể hủy (PAID/PENDING và suất chưa bắt đầu) */
    private boolean cancellable;
}
