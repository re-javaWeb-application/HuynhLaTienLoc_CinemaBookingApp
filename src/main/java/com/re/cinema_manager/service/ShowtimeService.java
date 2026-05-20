package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.ShowtimeRequestDTO;
import com.re.cinema_manager.model.entity.Showtime;

import java.util.List;

/**
 * CORE-05 — Quản lý suất chiếu (Showtime Scheduling).
 */
public interface ShowtimeService {

    List<Showtime> findAllShowtimes();

    Showtime getShowtimeById(Long id);

    Showtime createShowtime(ShowtimeRequestDTO dto);

    Showtime updateShowtime(Long id, ShowtimeRequestDTO dto);

    void deleteShowtime(Long id);
}
