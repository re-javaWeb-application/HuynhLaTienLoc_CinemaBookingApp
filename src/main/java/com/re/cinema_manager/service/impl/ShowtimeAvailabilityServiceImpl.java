package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.repository.SeatRepository;
import com.re.cinema_manager.repository.TicketRepository;
import com.re.cinema_manager.service.ShowtimeAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShowtimeAvailabilityServiceImpl implements ShowtimeAvailabilityService {

    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public long countTotalSeatsInRoom(Long roomId) {
        return seatRepository.countByRoomId(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBookedSeats(Long showtimeId) {
        return ticketRepository.countActiveTicketsByShowtimeId(showtimeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSoldOut(Long showtimeId, Long roomId) {
        long total = countTotalSeatsInRoom(roomId);
        if (total == 0) {
            return false;
        }
        return countBookedSeats(showtimeId) >= total;
    }
}
