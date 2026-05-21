package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.showtime.ShowtimeRequestDTO;
import com.re.cinema_manager.dto.showtime.MovieOptionDto;
import com.re.cinema_manager.dto.showtime.RoomOptionDto;
import com.re.cinema_manager.dto.showtime.ShowtimeListItemDto;

import java.util.List;

public interface ShowtimeService {

    List<ShowtimeListItemDto> listShowtimesForAdmin();

    List<MovieOptionDto> listMovieOptions();

    List<RoomOptionDto> listRoomOptions();

    ShowtimeRequestDTO getShowtimeRequestById(Long id);

    void createShowtime(ShowtimeRequestDTO dto);

    void updateShowtime(Long id, ShowtimeRequestDTO dto);

    void deleteShowtime(Long id);
}
