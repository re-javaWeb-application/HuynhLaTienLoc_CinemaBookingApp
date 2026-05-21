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
    /** Có thể hủy theo BookingPolicy (PENDING: trước giờ chiếu; PAID: trước 24h) */
    private boolean cancellable;
    /** Lý do không hủy được (hiển thị khi cancellable = false và đơn chưa CANCELLED) */
    private String cancelBlockReason;
    /** ONLINE | COUNTER_PENDING | COUNTER_PAID */
    private String paymentChannel;
}
