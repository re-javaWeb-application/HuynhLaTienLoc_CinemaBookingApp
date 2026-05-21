package com.re.cinema_manager.dto.staff;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StaffBookingDetailDto {

    private Long bookingId;
    private String bookingCode;
    private String bookingStatus;
    private String customerUsername;
    private String customerFullName;
    private String customerPhone;
    private String movieTitle;
    private String roomName;
    private LocalDateTime showtimeStart;
    private List<String> seatNames;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime bookedAt;
    private boolean canConfirmPayment;
    private boolean canPrintTicket;
}
