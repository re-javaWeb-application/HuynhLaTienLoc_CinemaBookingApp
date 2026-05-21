package com.re.cinema_manager.dto.staff;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StaffRoomShowtimeOptionDto {

    private Long showtimeId;
    private String movieTitle;
    private LocalDateTime startTime;
    private long bookedSeats;
    private long totalSeats;
}
