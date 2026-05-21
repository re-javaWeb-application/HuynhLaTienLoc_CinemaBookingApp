package com.re.cinema_manager.dto.staff;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StaffDashboardDto {

    private int totalRooms;
    private long totalSeatsInSystem;
    private long totalBookedTicketsUpcoming;
    private long totalUpcomingShowtimes;
    private List<RoomOccupancyDto> roomStats;
    private List<ShowtimeOccupancyDto> showtimeDetails;
}
