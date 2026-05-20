package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.GenreSectionView;
import com.re.cinema_manager.model.dto.UpcomingMovieView;

import java.util.List;

public interface CustomerHomeService {

    List<GenreSectionView> getUpcomingMoviesByGenre();

    UpcomingMovieView getShowtimeForBooking(Long showtimeId);
}
