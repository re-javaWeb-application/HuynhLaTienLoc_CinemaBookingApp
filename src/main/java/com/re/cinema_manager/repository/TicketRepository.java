package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            SELECT COUNT(t) FROM Ticket t
            WHERE t.showtime.id = :showtimeId
            AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            """)
    long countActiveTicketsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("""
            SELECT t.seat.id FROM Ticket t
            WHERE t.showtime.id = :showtimeId AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            """)
    Set<Long> findBookedSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("""
            SELECT t.seat.id FROM Ticket t
            WHERE t.showtime.id = :showtimeId AND t.seat.id IN :seatIds
            AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            """)
    List<Long> findBookedSeatIdsAmong(@Param("showtimeId") Long showtimeId, @Param("seatIds") Collection<Long> seatIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Ticket t WHERE t.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);

    @Query("""
            SELECT DISTINCT t FROM Ticket t
            JOIN FETCH t.seat
            WHERE t.booking.id IN :bookingIds
            ORDER BY t.seat.seatName
            """)
    List<Ticket> findByBookingIdInWithSeat(@Param("bookingIds") Collection<Long> bookingIds);
}
