package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.movie.MovieRequestDTO;
import com.re.cinema_manager.dto.movie.GenreOptionDto;
import com.re.cinema_manager.dto.movie.MovieListItemDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface MovieService {

    List<MovieListItemDto> listMoviesForAdmin();

    List<GenreOptionDto> listGenreOptions();

    MovieRequestDTO getMovieRequestById(Long movieId);

    @Transactional
    void createMovie(MovieRequestDTO dto);

    @Transactional
    void updateMovie(Long movieId, MovieRequestDTO dto);

    @Transactional
    void deletedMovie(Long movieId);
}
