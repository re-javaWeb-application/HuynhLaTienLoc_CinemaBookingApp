package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    /**
     * Lấy toàn bộ suất trong một phòng (kèm phim) để kiểm tra xung đột CORE-05.
     */
    @Query("""
            SELECT s FROM Showtime s
            JOIN FETCH s.movie
            JOIN FETCH s.room
            WHERE s.room.id = :roomId
            ORDER BY s.startTime ASC
            """)
    List<Showtime> findByRoomIdWithMovieAndRoom(@Param("roomId") Long roomId);

    @Query("""
            SELECT s FROM Showtime s
            JOIN FETCH s.movie
            JOIN FETCH s.room
            ORDER BY s.startTime DESC
            """)
    List<Showtime> findAllWithMovieAndRoom();

    @Query("""
            SELECT s FROM Showtime s
            JOIN FETCH s.movie
            JOIN FETCH s.room
            WHERE s.id = :id
            """)
    Optional<Showtime> findByIdWithMovieAndRoom(@Param("id") Long id);

    /** Suất chiếu từ thời điểm hiện tại trở đi (phim sắp chiếu). */
    @Query("""
            SELECT s FROM Showtime s
            JOIN FETCH s.movie m
            LEFT JOIN FETCH m.genre
            JOIN FETCH s.room
            WHERE s.startTime >= :from
            ORDER BY s.startTime ASC
            """)
    List<Showtime> findUpcomingWithMovieGenreAndRoom(@Param("from") LocalDateTime from);
}
