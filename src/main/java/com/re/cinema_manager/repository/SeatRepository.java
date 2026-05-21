package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRoomIdOrderBySeatNameAsc(Long roomId);

    long countByRoomId(Long roomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds AND s.room.id = :roomId ORDER BY s.id")
    List<Seat> findByIdInAndRoomIdForUpdate(@Param("seatIds") List<Long> seatIds, @Param("roomId") Long roomId);
}
