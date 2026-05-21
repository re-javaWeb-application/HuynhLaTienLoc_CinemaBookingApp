package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.staff.RoomOccupancyDto;
import com.re.cinema_manager.dto.staff.ShowtimeOccupancyDto;
import com.re.cinema_manager.dto.staff.StaffDashboardDto;
import com.re.cinema_manager.repository.RoomRepository;
import com.re.cinema_manager.repository.StaffDashboardRepository;
import com.re.cinema_manager.service.StaffDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffDashboardServiceImpl implements StaffDashboardService {

    private final StaffDashboardRepository staffDashboardRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public StaffDashboardDto buildDashboard() {
        LocalDateTime from = LocalDateTime.now();

        List<RoomOccupancyDto> roomStats = new ArrayList<>();
        for (Object[] row : staffDashboardRepository.aggregateRoomOccupancy(from)) {
            long roomId = ((Number) row[0]).longValue();
            String roomName = (String) row[1];
            int capacity = ((Number) row[2]).intValue();
            long totalSeats = row[3] != null ? ((Number) row[3]).longValue() : 0;
            long booked = row[4] != null ? ((Number) row[4]).longValue() : 0;
            double pct = totalSeats > 0 ? (booked * 100.0 / totalSeats) : 0;
            roomStats.add(RoomOccupancyDto.builder()
                    .roomId(roomId)
                    .roomName(roomName)
                    .capacity(capacity)
                    .totalSeats(totalSeats)
                    .bookedSeatsUpcoming(booked)
                    .occupancyPercent(Math.round(pct * 10) / 10.0)
                    .build());
        }

        List<ShowtimeOccupancyDto> showtimeDetails = new ArrayList<>();
        for (Object[] row : staffDashboardRepository.aggregateShowtimeOccupancy(from)) {
            long showtimeId = ((Number) row[0]).longValue();
            String movieTitle = (String) row[1];
            String roomName = (String) row[2];
            LocalDateTime start = (LocalDateTime) row[3];
            long booked = row[4] != null ? ((Number) row[4]).longValue() : 0;
            long total = row[5] != null ? ((Number) row[5]).longValue() : 0;
            double pct = total > 0 ? (booked * 100.0 / total) : 0;
            showtimeDetails.add(ShowtimeOccupancyDto.builder()
                    .showtimeId(showtimeId)
                    .movieTitle(movieTitle)
                    .roomName(roomName)
                    .startTime(start)
                    .bookedSeats(booked)
                    .totalSeats(total)
                    .occupancyPercent(Math.round(pct * 10) / 10.0)
                    .build());
        }

        return StaffDashboardDto.builder()
                .totalRooms((int) roomRepository.count())
                .totalSeatsInSystem(staffDashboardRepository.countAllSeats())
                .totalBookedTicketsUpcoming(staffDashboardRepository.countBookedTicketsUpcoming(from))
                .totalUpcomingShowtimes(staffDashboardRepository.countUpcomingShowtimes(from))
                .roomStats(roomStats)
                .showtimeDetails(showtimeDetails)
                .build();
    }
}
