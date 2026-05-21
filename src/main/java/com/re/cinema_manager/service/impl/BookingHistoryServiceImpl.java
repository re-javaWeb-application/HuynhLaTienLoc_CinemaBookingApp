package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.booking.BookingHistoryItemDto;
import com.re.cinema_manager.model.entity.Booking;
import com.re.cinema_manager.model.entity.BookingStatus;
import com.re.cinema_manager.model.entity.Ticket;
import com.re.cinema_manager.repository.BookingRepository;
import com.re.cinema_manager.repository.TicketRepository;
import com.re.cinema_manager.service.BookingHistoryService;
import com.re.cinema_manager.util.BookingPolicy;
import com.re.cinema_manager.util.PosterUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CORE-07: Lịch sử đặt vé — EntityGraph tránh N+1 trên showtime/movie/room/payment,
 * batch load tickets theo danh sách booking id trên trang hiện tại.
 */
@Service
@RequiredArgsConstructor
public class BookingHistoryServiceImpl implements BookingHistoryService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BookingHistoryItemDto> getBookingHistory(Long userId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findHistoryByUserAndStatuses(
                userId,
                List.of(BookingStatus.PAID, BookingStatus.PENDING, BookingStatus.CANCELLED),
                pageable);

        if (page.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Long> bookingIds = page.getContent().stream().map(Booking::getId).toList();
        Map<Long, List<Ticket>> ticketsByBooking = ticketRepository.findByBookingIdInWithSeat(bookingIds).stream()
                .collect(Collectors.groupingBy(t -> t.getBooking().getId()));

        List<BookingHistoryItemDto> items = page.getContent().stream()
                .map(b -> toHistoryItem(b, ticketsByBooking.getOrDefault(b.getId(), List.of())))
                .toList();

        return new PageImpl<>(items, pageable, page.getTotalElements());
    }

    private BookingHistoryItemDto toHistoryItem(Booking booking, List<Ticket> tickets) {
        var st = booking.getShowtime();
        var movie = st.getMovie();
        List<String> seatNames = tickets.stream()
                .map(t -> t.getSeat().getSeatName())
                .sorted()
                .toList();

        String paymentStatus = booking.getPayment() != null
                ? booking.getPayment().getStatus().name()
                : (booking.getStatus() == BookingStatus.PENDING ? "CHUA_THANH_TOAN" : "—");

        boolean cancellable = isCancellable(booking);

        return BookingHistoryItemDto.builder()
                .bookingId(booking.getId())
                .bookingCode("BK-" + booking.getId())
                .movieTitle(movie.getTitle())
                .moviePoster(PosterUrlUtil.normalize(movie.getPosterUrl()))
                .cinemaName(BookingServiceImpl.CINEMA_NAME)
                .roomName(st.getRoom().getRoomName())
                .showtimeStart(st.getStartTime())
                .seatNames(seatNames)
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(paymentStatus)
                .bookedAt(booking.getCreatedAt())
                .bookingStatus(booking.getStatus().name())
                .cancellable(cancellable)
                .build();
    }

    private static boolean isCancellable(Booking booking) {
        if (booking.getStatus() != BookingStatus.PAID && booking.getStatus() != BookingStatus.PENDING) {
            return false;
        }
        return BookingPolicy.canCancelByTime(booking.getShowtime().getStartTime());
    }
}
