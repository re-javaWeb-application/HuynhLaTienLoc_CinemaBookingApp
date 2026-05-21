package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Booking;
import com.re.cinema_manager.model.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"showtime", "showtime.movie", "showtime.room", "payment"})
    @Query("""
            SELECT b FROM Booking b
            WHERE b.user.id = :userId AND b.status IN :statuses
            ORDER BY b.createdAt DESC
            """)
    Page<Booking> findHistoryByUserAndStatuses(
            @Param("userId") Long userId,
            @Param("statuses") Collection<BookingStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.showtime st
            JOIN FETCH st.movie
            JOIN FETCH st.room
            LEFT JOIN FETCH b.payment
            LEFT JOIN FETCH b.tickets tk
            JOIN FETCH tk.seat
            WHERE b.id = :id AND b.user.id = :userId
            """)
    Optional<Booking> findByIdAndUserIdWithDetails(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.user u
            LEFT JOIN FETCH u.userProfile
            JOIN FETCH b.showtime st
            JOIN FETCH st.movie
            JOIN FETCH st.room
            LEFT JOIN FETCH b.payment
            LEFT JOIN FETCH b.tickets tk
            JOIN FETCH tk.seat
            WHERE b.id = :id
            """)
    Optional<Booking> findByIdWithFullDetails(@Param("id") Long id);

    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.status = com.re.cinema_manager.model.entity.BookingStatus.CANCELLED
              AND b.createdAt >= :from AND b.createdAt < :to
            """)
    long countCancelledBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    long countByUserId(Long userId);

    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.showtime.movie.id = :movieId
            AND b.status IN (
                com.re.cinema_manager.model.entity.BookingStatus.PAID,
                com.re.cinema_manager.model.entity.BookingStatus.PENDING
            )
            """)
    long countActiveBookingsByMovieId(@Param("movieId") Long movieId);

    void deleteByShowtime_Movie_Id(Long movieId);
}
