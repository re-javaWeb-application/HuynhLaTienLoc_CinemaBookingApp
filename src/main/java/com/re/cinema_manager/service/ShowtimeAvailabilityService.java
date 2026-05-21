package com.re.cinema_manager.service;

/**
 * CORE-08: Kiểm tra ghế còn trống / hết vé theo suất chiếu.
 */
public interface ShowtimeAvailabilityService {

    long countTotalSeatsInRoom(Long roomId);

    long countBookedSeats(Long showtimeId);

    boolean isSoldOut(Long showtimeId, Long roomId);
}
