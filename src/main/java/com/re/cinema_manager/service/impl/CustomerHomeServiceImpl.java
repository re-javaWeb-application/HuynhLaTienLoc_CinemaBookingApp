package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.model.dto.GenreSectionView;
import com.re.cinema_manager.model.dto.UpcomingMovieView;
import com.re.cinema_manager.model.entity.Genre;
import com.re.cinema_manager.model.entity.Showtime;
import com.re.cinema_manager.repository.ShowtimeRepository;
import com.re.cinema_manager.service.CustomerHomeService;
import com.re.cinema_manager.service.ShowtimeAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerHomeServiceImpl implements CustomerHomeService {

    private static final long NO_GENRE_ID = 0L;
    private static final String NO_GENRE_NAME = "Khác";

    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeAvailabilityService showtimeAvailabilityService;

    @Override
    public List<GenreSectionView> getUpcomingMoviesByGenre() {
        LocalDateTime now = LocalDateTime.now();
        List<Showtime> upcoming = showtimeRepository.findUpcomingWithMovieGenreAndRoom(now);

        // genreId -> (movieId -> card giữ suất sớm nhất)
        Map<Long, GenreSectionView> sectionMap = new LinkedHashMap<>();
        Map<Long, Map<Long, UpcomingMovieView>> movieByGenre = new HashMap<>();

        for (Showtime showtime : upcoming) {
            Genre genre = showtime.getMovie().getGenre();
            long genreId = genre != null ? genre.getId() : NO_GENRE_ID;
            String genreName = genre != null ? genre.getName() : NO_GENRE_NAME;

            sectionMap.computeIfAbsent(genreId, id -> GenreSectionView.builder()
                    .genreId(genreId)
                    .genreName(genreName)
                    .movies(new ArrayList<>())
                    .build());

            Map<Long, UpcomingMovieView> moviesInGenre = movieByGenre.computeIfAbsent(
                    genreId, id -> new LinkedHashMap<>());

            long movieId = showtime.getMovie().getId();
            UpcomingMovieView candidate = toView(showtime, genreId, genreName);

            UpcomingMovieView existing = moviesInGenre.get(movieId);
            if (existing == null || candidate.getStartTime().isBefore(existing.getStartTime())) {
                moviesInGenre.put(movieId, candidate);
            }
        }

        List<GenreSectionView> result = new ArrayList<>();
        for (GenreSectionView section : sectionMap.values()) {
            Map<Long, UpcomingMovieView> movies = movieByGenre.get(section.getGenreId());
            if (movies != null && !movies.isEmpty()) {
                List<UpcomingMovieView> sorted = new ArrayList<>(movies.values());
                sorted.sort(Comparator.comparing(UpcomingMovieView::getStartTime));
                section.setMovies(sorted);
                result.add(section);
            }
        }

        result.sort(Comparator.comparing(GenreSectionView::getGenreName));
        return result;
    }

    @Override
    public UpcomingMovieView getShowtimeForBooking(Long showtimeId) {
        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu."));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Suất chiếu đã qua, không thể đặt vé.");
        }

        Genre genre = showtime.getMovie().getGenre();
        long genreId = genre != null ? genre.getId() : NO_GENRE_ID;
        String genreName = genre != null ? genre.getName() : NO_GENRE_NAME;
        return toView(showtime, genreId, genreName);
    }

    private UpcomingMovieView toView(Showtime showtime, long genreId, String genreName) {
        var movie = showtime.getMovie();
        Long roomId = showtime.getRoom().getId();
        long total = showtimeAvailabilityService.countTotalSeatsInRoom(roomId);
        long booked = showtimeAvailabilityService.countBookedSeats(showtime.getId());
        boolean soldOut = showtimeAvailabilityService.isSoldOut(showtime.getId(), roomId);

        return UpcomingMovieView.builder()
                .showtimeId(showtime.getId())
                .movieId(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .durationMinutes(movie.getDurationMinutes())
                .genreId(genreId)
                .genreName(genreName)
                .startTime(showtime.getStartTime())
                .roomName(showtime.getRoom().getRoomName())
                .soldOut(soldOut)
                .totalSeats((int) total)
                .bookedSeats((int) booked)
                .build();
    }
}
