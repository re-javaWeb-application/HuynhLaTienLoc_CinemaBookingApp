package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.MovieRequestDTO;
import com.re.cinema_manager.model.entity.Movie;
import jakarta.transaction.Transactional;

import java.util.List;

public interface MovieService {

    //Tra ve danh sach film
    List<Movie> showAllMovie();

    //them 1 bo phim moi
    @Transactional
    Movie createMovie(MovieRequestDTO dto);

    //
    Movie getMovieById(Long movieId);

    @Transactional
    Movie updateMovie(Long movieId, MovieRequestDTO dto);

    @Transactional
    void deletedMovie(Long movieId);

}
