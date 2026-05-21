package com.re.cinema_manager.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class SeatSelectionView {

    private Long showtimeId;
    private String movieTitle;
    private String moviePoster;
    private String roomName;
    private LocalDateTime showtimeStart;
    private int durationMinutes;
    private List<SeatRowView> seats;
    private Set<Long> bookedSeatIds;
    private BigDecimal standardPrice;
    /** CORE-08 */
    private boolean soldOut;
    private int totalSeats;
    private int bookedSeats;
    private int availableSeats;
}
