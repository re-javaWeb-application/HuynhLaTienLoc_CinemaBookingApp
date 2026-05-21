package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.booking.*;
import com.re.cinema_manager.exception.InvalidBookingRequestException;
import com.re.cinema_manager.exception.SeatAlreadyBookedException;
import com.re.cinema_manager.model.entity.*;
import com.re.cinema_manager.repository.*;
import com.re.cinema_manager.service.BookingService;
import com.re.cinema_manager.service.ShowtimeAvailabilityService;
import com.re.cinema_manager.util.BookingCodeUtil;
import com.re.cinema_manager.util.BookingPolicy;
import com.re.cinema_manager.util.PosterUrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CORE-06: Thanh toán vé trong một transaction duy nhất.
 * Lock ghế (PESSIMISTIC_WRITE) → kiểm tra → booking + payment + tickets → commit.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    public static final BigDecimal STANDARD_SEAT_PRICE = new BigDecimal("85000");
    public static final String CINEMA_NAME = "CineMax Cinema";

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowtimeAvailabilityService showtimeAvailabilityService;

    @Override
    @Transactional(readOnly = true)
    public SeatSelectionView getSeatSelection(Long showtimeId) {
        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(showtimeId)
                .orElseThrow(() -> new InvalidBookingRequestException("Suất chiếu không tồn tại."));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingRequestException("Suất chiếu đã kết thúc hoặc đang diễn ra.");
        }

        Long roomId = showtime.getRoom().getId();
        Set<Long> bookedIds = ticketRepository.findBookedSeatIdsByShowtimeId(showtimeId);

        List<SeatRowView> seatRows = seatRepository.findByRoomIdOrderBySeatNameAsc(roomId).stream()
                .map(s -> SeatRowView.builder()
                        .id(s.getId())
                        .seatName(s.getSeatName())
                        .booked(bookedIds.contains(s.getId()))
                        .build())
                .toList();

        Movie movie = showtime.getMovie();
        long total = showtimeAvailabilityService.countTotalSeatsInRoom(roomId);
        long booked = bookedIds.size();
        boolean soldOut = showtimeAvailabilityService.isSoldOut(showtimeId, roomId);
        int available = (int) Math.max(0, total - booked);

        return SeatSelectionView.builder()
                .showtimeId(showtimeId)
                .movieTitle(movie.getTitle())
                .moviePoster(PosterUrlUtil.normalize(movie.getPosterUrl()))
                .roomName(showtime.getRoom().getRoomName())
                .showtimeStart(showtime.getStartTime())
                .durationMinutes(movie.getDurationMinutes())
                .seats(seatRows)
                .bookedSeatIds(bookedIds)
                .standardPrice(STANDARD_SEAT_PRICE)
                .soldOut(soldOut)
                .totalSeats((int) total)
                .bookedSeats((int) booked)
                .availableSeats(available)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingInvoiceDto createBooking(Long userId, CreateBookingRequest request) {
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new InvalidBookingRequestException("Vui lòng chọn ít nhất một ghế.");
        }

        List<Long> seatIds = request.getSeatIds().stream().distinct().sorted().toList();
        if (seatIds.size() != request.getSeatIds().size()) {
            throw new InvalidBookingRequestException("Danh sách ghế không hợp lệ (trùng lặp).");
        }

        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(request.getShowtimeId())
                .orElseThrow(() -> new InvalidBookingRequestException("Suất chiếu không tồn tại."));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingRequestException("Không thể đặt vé cho suất đã qua.");
        }
        if (showtimeAvailabilityService.isSoldOut(showtime.getId(), showtime.getRoom().getId())) {
            throw new InvalidBookingRequestException("Suất chiếu đã hết vé.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidBookingRequestException("Người dùng không tồn tại."));

        Long roomId = showtime.getRoom().getId();

        // Bước 1: PESSIMISTIC_WRITE — khóa hàng ghế trong transaction, chặn double booking đồng thời
        List<Seat> lockedSeats = seatRepository.findByIdInAndRoomIdForUpdate(seatIds, roomId);
        if (lockedSeats.size() != seatIds.size()) {
            throw new InvalidBookingRequestException("Một hoặc nhiều ghế không thuộc phòng của suất chiếu này.");
        }

        // Bước 2: Kiểm tra ghế đã có vé trong suất này chưa
        List<Long> alreadyBooked = ticketRepository.findBookedSeatIdsAmong(showtime.getId(), seatIds);
        if (!alreadyBooked.isEmpty()) {
            Map<Long, String> idToName = lockedSeats.stream()
                    .collect(Collectors.toMap(Seat::getId, Seat::getSeatName));
            List<String> takenNames = alreadyBooked.stream()
                    .map(idToName::get)
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
            log.warn("Rollback booking: seats taken showtime={} seats={}", showtime.getId(), takenNames);
            throw new SeatAlreadyBookedException(takenNames);
        }

        BigDecimal unitPrice = STANDARD_SEAT_PRICE;
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(seatIds.size()));
        boolean payAtCounter = PAYMENT_AT_COUNTER.equalsIgnoreCase(
                request.getPaymentMethod() != null ? request.getPaymentMethod().trim() : "");

        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .totalAmount(total)
                .status(payAtCounter ? BookingStatus.PENDING : BookingStatus.PAID)
                .build();

        for (Seat seat : lockedSeats) {
            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .showtime(showtime)
                    .seat(seat)
                    .unitPrice(unitPrice)
                    .build();
            booking.getTickets().add(ticket);
        }

        if (!payAtCounter) {
            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(total)
                    .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH")
                    .status(PaymentStatus.SUCCESS)
                    .paidAt(LocalDateTime.now())
                    .build();
            booking.setPayment(payment);
        }

        Booking saved;
        try {
            saved = bookingRepository.save(booking);
        } catch (DataIntegrityViolationException ex) {
            // Race: UNIQUE (showtime_id, seat_id) — rollback toàn bộ transaction
            List<String> names = lockedSeats.stream().map(Seat::getSeatName).sorted().toList();
            log.warn("UK violation on ticket insert, rollback: {}", ex.getMessage());
            throw new SeatAlreadyBookedException(names);
        }
        log.info("Booking committed id={} user={} seats={}", saved.getId(), userId, seatIds.size());

        return toInvoice(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserIdWithDetails(bookingId, userId)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy đơn đặt vé."));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingRequestException("Đơn đặt vé đã được hủy trước đó.");
        }
        if (booking.getStatus() != BookingStatus.PAID && booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingRequestException("Không thể hủy đơn ở trạng thái hiện tại.");
        }
        var cancelDecision = BookingPolicy.evaluateCancel(
                booking.getStatus(), booking.getShowtime().getStartTime());
        if (!cancelDecision.allowed()) {
            throw new InvalidBookingRequestException(BookingPolicy.cancelDeniedMessage(cancelDecision));
        }

        ticketRepository.deleteByBookingId(booking.getId());
        booking.getTickets().clear();

        if (booking.getPayment() != null) {
            booking.getPayment().setStatus(PaymentStatus.FAILED);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled id={} user={}", bookingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingInvoiceDto getInvoiceByIdAndUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserIdWithDetails(bookingId, userId)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy hóa đơn."));
        return toInvoice(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingInvoiceDto getInvoiceById(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithFullDetails(bookingId)
                .orElseThrow(() -> new InvalidBookingRequestException("Không tìm thấy đơn đặt vé."));
        return toInvoice(booking);
    }

    private BookingInvoiceDto toInvoice(Booking booking) {
        Showtime st = booking.getShowtime();
        Movie movie = st.getMovie();
        List<String> seatNames = booking.getTickets().stream()
                .map(t -> t.getSeat().getSeatName())
                .sorted()
                .toList();

        Payment payment = booking.getPayment();
        String customerName = null;
        if (booking.getUser().getUserProfile() != null) {
            customerName = booking.getUser().getUserProfile().getFullName();
        }
        return BookingInvoiceDto.builder()
                .bookingId(booking.getId())
                .bookingCode(BookingCodeUtil.format(booking.getId()))
                .movieTitle(movie.getTitle())
                .moviePoster(PosterUrlUtil.normalize(movie.getPosterUrl()))
                .cinemaName(CINEMA_NAME)
                .roomName(st.getRoom().getRoomName())
                .showtimeStart(st.getStartTime())
                .seatNames(seatNames)
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(payment != null ? payment.getStatus().name() : "PENDING")
                .paymentMethod(payment != null ? payment.getPaymentMethod() : BookingService.PAYMENT_AT_COUNTER)
                .paidAt(payment != null ? payment.getPaidAt() : null)
                .bookedAt(booking.getCreatedAt())
                .bookingStatus(booking.getStatus().name())
                .customerUsername(booking.getUser().getUsername())
                .customerName(customerName)
                .build();
    }
}
