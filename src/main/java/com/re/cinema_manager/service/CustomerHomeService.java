package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.customer.GenreSectionView;
import com.re.cinema_manager.dto.customer.UpcomingMovieView;

import java.util.List;

public interface CustomerHomeService {

    List<GenreSectionView> getUpcomingMoviesByGenre();

    UpcomingMovieView getShowtimeForBooking(Long showtimeId);
}
