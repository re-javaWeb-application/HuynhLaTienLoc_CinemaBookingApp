package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.booking.BookingInvoiceDto;
import com.re.cinema_manager.dto.staff.StaffBookingDetailDto;
import com.re.cinema_manager.exception.InvalidBookingRequestException;
import com.re.cinema_manager.model.entity.*;
import com.re.cinema_manager.repository.BookingRepository;
import com.re.cinema_manager.service.BookingService;
import com.re.cinema_manager.service.StaffBookingService;
import com.re.cinema_manager.util.BookingCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffBookingServiceImpl implements StaffBookingService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Override
    @Transactional(readOnly = true)
    public StaffBookingDetailDto lookupByCode(String bookingCode) {
        Long id = BookingCodeUtil.parseId(bookingCode);
        if (id == null) {
            throw new InvalidBookingRequestException("Mã vé không hợp lệ. Nhập dạng BK-123 hoặc 123.");
        }
        Booking booking = bookingRepository.findByIdWithFullDetails(id)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy đơn với mã " + BookingCodeUtil.format(id)));

        return toStaffDetail(booking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingInvoiceDto confirmCounterPayment(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithFullDetails(bookingId)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy đơn đặt vé."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingRequestException(
                    "Chỉ xác nhận thanh toán cho đơn đang chờ (PENDING). Trạng thái hiện tại: " + booking.getStatus());
        }
        if (!booking.getShowtime().getStartTime().isAfter(LocalDateTime.now())) {
            throw new InvalidBookingRequestException("Suất chiếu đã qua — không thể xác nhận thanh toán.");
        }

        Payment payment = booking.getPayment();
        if (payment == null) {
            payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .paymentMethod("CASH_COUNTER")
                    .status(PaymentStatus.SUCCESS)
                    .paidAt(LocalDateTime.now())
                    .build();
            booking.setPayment(payment);
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setPaymentMethod("CASH_COUNTER");
        }

        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
        log.info("Staff confirmed counter payment bookingId={}", bookingId);

        return bookingService.getInvoiceById(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingInvoiceDto getPrintableInvoice(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithFullDetails(bookingId)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy đơn đặt vé."));
        if (booking.getStatus() != BookingStatus.PAID) {
            throw new InvalidBookingRequestException("Chỉ in vé khi đơn đã thanh toán (PAID).");
        }
        return bookingService.getInvoiceById(bookingId);
    }

    private StaffBookingDetailDto toStaffDetail(Booking booking) {
        var st = booking.getShowtime();
        List<String> seatNames = booking.getTickets().stream()
                .map(t -> t.getSeat().getSeatName())
                .sorted()
                .toList();

        User user = booking.getUser();
        UserProfile profile = user.getUserProfile();
        String fullName = profile != null ? profile.getFullName() : user.getUsername();
        String phone = profile != null ? profile.getPhone() : null;

        Payment payment = booking.getPayment();
        boolean pending = booking.getStatus() == BookingStatus.PENDING;
        boolean paid = booking.getStatus() == BookingStatus.PAID;
        boolean showtimeFuture = st.getStartTime().isAfter(LocalDateTime.now());

        return StaffBookingDetailDto.builder()
                .bookingId(booking.getId())
                .bookingCode(BookingCodeUtil.format(booking.getId()))
                .bookingStatus(booking.getStatus().name())
                .customerUsername(user.getUsername())
                .customerFullName(fullName)
                .customerPhone(phone)
                .movieTitle(st.getMovie().getTitle())
                .roomName(st.getRoom().getRoomName())
                .showtimeStart(st.getStartTime())
                .seatNames(seatNames)
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(payment != null ? payment.getStatus().name() : "CHUA_THANH_TOAN")
                .paymentMethod(payment != null ? payment.getPaymentMethod() : BookingService.PAYMENT_AT_COUNTER)
                .paidAt(payment != null ? payment.getPaidAt() : null)
                .bookedAt(booking.getCreatedAt())
                .canConfirmPayment(pending && showtimeFuture)
                .canPrintTicket(paid)
                .build();
    }
}
