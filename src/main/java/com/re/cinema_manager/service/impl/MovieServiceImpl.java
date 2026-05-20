package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.model.dto.MovieRequestDTO;
import com.re.cinema_manager.model.entity.Genre;
import com.re.cinema_manager.model.entity.Movie;
import com.re.cinema_manager.repository.GenreRepository;
import com.re.cinema_manager.repository.MovieRepository;
import com.re.cinema_manager.service.MovieService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Override
    //Tra ve danh sach film
    public List<Movie> showAllMovie(){
        return movieRepository.findAllWithGenre();
    }

    @Override
    //them 1 bo phim moi
    @Transactional
    public Movie createMovie(MovieRequestDTO dto){
        Genre genre =  genreRepository.findById(dto.getGenreId()).orElseThrow(()-> new IllegalArgumentException(("Khong Tim thay the loai phim nay")));

        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(dto.getReleaseDate())
                .posterUrl(dto.getPosterUrl())
                .genre(genre)
                .build();
        return movieRepository.save(movie);
    }

    @Override
    //
    @Transactional
    public Movie getMovieById(Long movieId){
        return movieRepository.findByIdWithGenre(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phim nay"));
    }

    @Override
    @Transactional
    public Movie updateMovie(Long movieId, MovieRequestDTO dto){
        Movie existingMovie = getMovieById(movieId);

        Genre genre =  genreRepository.findById(dto.getGenreId()).orElseThrow(()-> new IllegalArgumentException(("Khong Tim thay the loai phim nay")));

        //Ghi de thong tin cua movie moi update vao entity cu vao bo nho tam
        existingMovie.setTitle(dto.getTitle());
        existingMovie.setDescription(dto.getDescription());
        existingMovie.setDurationMinutes(dto.getDurationMinutes());
        existingMovie.setReleaseDate(dto.getReleaseDate());
        existingMovie.setPosterUrl(dto.getPosterUrl());
        existingMovie.setGenre(genre);

        return movieRepository.save(existingMovie);
    }

    @Override
    @Transactional
    public void deletedMovie(Long movieId){
        if(!movieRepository.existsById(movieId)){
            throw new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }

}
