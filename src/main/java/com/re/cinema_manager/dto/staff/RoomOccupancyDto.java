package com.re.cinema_manager.dto.staff;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomOccupancyDto {

    private Long roomId;
    private String roomName;
    private int capacity;
    private long totalSeats;
    private long bookedSeatsUpcoming;
    private double occupancyPercent;
}
