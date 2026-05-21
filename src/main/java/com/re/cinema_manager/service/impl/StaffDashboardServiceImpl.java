package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.booking.SeatRowView;
import com.re.cinema_manager.dto.staff.*;
import com.re.cinema_manager.exception.InvalidBookingRequestException;
import com.re.cinema_manager.model.entity.Room;
import com.re.cinema_manager.model.entity.Showtime;
import com.re.cinema_manager.repository.RoomRepository;
import com.re.cinema_manager.repository.SeatRepository;
import com.re.cinema_manager.repository.ShowtimeRepository;
import com.re.cinema_manager.repository.StaffDashboardRepository;
import com.re.cinema_manager.repository.TicketRepository;
import com.re.cinema_manager.service.StaffDashboardService;
import com.re.cinema_manager.util.BookingCodeUtil;
import com.re.cinema_manager.util.PosterUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StaffDashboardServiceImpl implements StaffDashboardService {

    private final StaffDashboardRepository staffDashboardRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

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
            long roomId = row[6] != null ? ((Number) row[6]).longValue() : 0;
            showtimeDetails.add(ShowtimeOccupancyDto.builder()
                    .showtimeId(showtimeId)
                    .roomId(roomId)
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

    @Override
    @Transactional(readOnly = true)
    public StaffRoomDetailDto getRoomDetail(Long roomId, Long showtimeId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new InvalidBookingRequestException("Phòng chiếu không tồn tại."));

        LocalDateTime from = LocalDateTime.now();
        List<Showtime> upcomingInRoom = showtimeRepository.findByRoomIdWithMovieAndRoom(roomId).stream()
                .filter(st -> !st.getStartTime().isBefore(from))
                .toList();

        List<StaffRoomShowtimeOptionDto> options = new ArrayList<>();
        for (Showtime st : upcomingInRoom) {
            long total = seatRepository.countByRoomId(roomId);
            long booked = ticketRepository.countActiveTicketsByShowtimeId(st.getId());
            options.add(StaffRoomShowtimeOptionDto.builder()
                    .showtimeId(st.getId())
                    .movieTitle(st.getMovie().getTitle())
                    .startTime(st.getStartTime())
                    .bookedSeats(booked)
                    .totalSeats(total)
                    .build());
        }

        Showtime selected = resolveSelectedShowtime(upcomingInRoom, showtimeId, roomId);
        long totalSeats = seatRepository.countByRoomId(roomId);

        if (selected == null) {
            List<SeatRowView> allSeats = seatRepository.findByRoomIdOrderBySeatNameAsc(roomId).stream()
                    .map(s -> SeatRowView.builder()
                            .id(s.getId())
                            .seatName(s.getSeatName())
                            .booked(false)
                            .build())
                    .toList();
            return StaffRoomDetailDto.builder()
                    .roomId(room.getId())
                    .roomName(room.getRoomName())
                    .capacity(room.getCapacity())
                    .totalSeats(totalSeats)
                    .showtimeOptions(options)
                    .seats(allSeats)
                    .bookedSeatDetails(List.of())
                    .bookedCount(0)
                    .availableCount(totalSeats)
                    .build();
        }

        Set<Long> bookedIds = ticketRepository.findBookedSeatIdsByShowtimeId(selected.getId());
        List<SeatRowView> seats = seatRepository.findByRoomIdOrderBySeatNameAsc(roomId).stream()
                .map(s -> SeatRowView.builder()
                        .id(s.getId())
                        .seatName(s.getSeatName())
                        .booked(bookedIds.contains(s.getId()))
                        .build())
                .toList();

        List<StaffBookedSeatDto> bookedDetails = new ArrayList<>();
        for (Object[] row : staffDashboardRepository.findBookedSeatDetailsByShowtimeId(selected.getId())) {
            Long bookingId = ((Number) row[2]).longValue();
            String customer = (String) row[4];
            bookedDetails.add(StaffBookedSeatDto.builder()
                    .seatName((String) row[1])
                    .bookingCode(BookingCodeUtil.format(bookingId))
                    .customerLabel(customer)
                    .bookingStatus(row[5].toString())
                    .build());
        }

        long bookedCount = bookedIds.size();
        return StaffRoomDetailDto.builder()
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .capacity(room.getCapacity())
                .totalSeats(totalSeats)
                .selectedShowtimeId(selected.getId())
                .selectedMovieTitle(selected.getMovie().getTitle())
                .selectedMoviePoster(PosterUrlUtil.normalize(selected.getMovie().getPosterUrl()))
                .selectedShowtimeStart(selected.getStartTime())
                .bookedCount(bookedCount)
                .availableCount(Math.max(0, totalSeats - bookedCount))
                .showtimeOptions(options)
                .seats(seats)
                .bookedSeatDetails(bookedDetails)
                .build();
    }

    private Showtime resolveSelectedShowtime(List<Showtime> upcomingInRoom, Long showtimeId, Long roomId) {
        if (upcomingInRoom.isEmpty()) {
            return null;
        }
        if (showtimeId != null) {
            return upcomingInRoom.stream()
                    .filter(st -> st.getId().equals(showtimeId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidBookingRequestException(
                            "Suất chiếu không thuộc phòng này hoặc đã qua."));
        }
        return upcomingInRoom.get(0);
    }
}
