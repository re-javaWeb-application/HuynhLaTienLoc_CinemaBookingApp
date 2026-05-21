package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.ShowtimeRequestDTO;
import com.re.cinema_manager.model.dto.admin.MovieOptionDto;
import com.re.cinema_manager.model.dto.admin.RoomOptionDto;
import com.re.cinema_manager.model.dto.admin.ShowtimeListItemDto;

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
