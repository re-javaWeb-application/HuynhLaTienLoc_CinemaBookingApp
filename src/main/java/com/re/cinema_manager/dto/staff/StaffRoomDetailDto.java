package com.re.cinema_manager.dto.staff;

import com.re.cinema_manager.dto.booking.SeatRowView;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StaffRoomDetailDto {

    private Long roomId;
    private String roomName;
    private int capacity;
    private long totalSeats;

    private Long selectedShowtimeId;
    private String selectedMovieTitle;
    private String selectedMoviePoster;
    private LocalDateTime selectedShowtimeStart;
    private long bookedCount;
    private long availableCount;

    private List<StaffRoomShowtimeOptionDto> showtimeOptions;
    private List<SeatRowView> seats;
    private List<StaffBookedSeatDto> bookedSeatDetails;
}
