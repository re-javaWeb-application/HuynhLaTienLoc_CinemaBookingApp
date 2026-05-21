package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StaffDashboardRepository extends JpaRepository<Showtime, Long> {

    @Query("""
            SELECT r.id, r.roomName, r.capacity,
                   COUNT(DISTINCT s.id),
                   COUNT(DISTINCT t.id)
            FROM Room r
            LEFT JOIN Seat s ON s.room.id = r.id
            LEFT JOIN Showtime st ON st.room.id = r.id AND st.startTime >= :from
            LEFT JOIN Ticket t ON t.showtime.id = st.id
                AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            GROUP BY r.id, r.roomName, r.capacity
            ORDER BY r.roomName
            """)
    List<Object[]> aggregateRoomOccupancy(@Param("from") LocalDateTime from);

    @Query("""
            SELECT st.id, m.title, r.roomName, st.startTime,
                   COUNT(DISTINCT t.id),
                   (SELECT COUNT(s2.id) FROM Seat s2 WHERE s2.room.id = r.id),
                   r.id
            FROM Showtime st
            JOIN st.movie m
            JOIN st.room r
            LEFT JOIN Ticket t ON t.showtime.id = st.id
                AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            WHERE st.startTime >= :from
            GROUP BY st.id, m.title, r.roomName, st.startTime, r.id
            ORDER BY st.startTime ASC
            """)
    List<Object[]> aggregateShowtimeOccupancy(@Param("from") LocalDateTime from);

    @Query("""
            SELECT COUNT(DISTINCT t.id) FROM Ticket t
            JOIN t.showtime st
            WHERE st.startTime >= :from
            AND t.booking.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            """)
    long countBookedTicketsUpcoming(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(st) FROM Showtime st WHERE st.startTime >= :from")
    long countUpcomingShowtimes(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(s) FROM Seat s")
    long countAllSeats();

    @Query("""
            SELECT t.seat.id, t.seat.seatName, b.id, u.username,
                   COALESCE(up.fullName, u.username), b.status
            FROM Ticket t
            JOIN t.booking b
            JOIN b.user u
            LEFT JOIN u.userProfile up
            WHERE t.showtime.id = :showtimeId
            AND b.status <> com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
            ORDER BY t.seat.seatName
            """)
    List<Object[]> findBookedSeatDetailsByShowtimeId(@Param("showtimeId") Long showtimeId);
}
