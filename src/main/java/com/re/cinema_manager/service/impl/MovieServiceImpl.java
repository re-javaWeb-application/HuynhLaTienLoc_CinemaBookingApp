package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.model.dto.MovieRequestDTO;
import com.re.cinema_manager.model.dto.admin.GenreOptionDto;
import com.re.cinema_manager.model.dto.admin.MovieListItemDto;
import com.re.cinema_manager.model.entity.Genre;
import com.re.cinema_manager.model.entity.Movie;
import com.re.cinema_manager.repository.BookingRepository;
import com.re.cinema_manager.repository.GenreRepository;
import com.re.cinema_manager.repository.MovieRepository;
import com.re.cinema_manager.repository.ShowtimeRepository;
import com.re.cinema_manager.service.MovieService;
import com.re.cinema_manager.util.PosterUrlUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<MovieListItemDto> listMoviesForAdmin() {
        return movieRepository.findAllWithGenre().stream()
                .map(this::toListItem)
                .toList();
    }

    @Override
    public List<GenreOptionDto> listGenreOptions() {
        return genreRepository.findAll().stream()
                .map(g -> GenreOptionDto.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .build())
                .toList();
    }

    @Override
    public MovieRequestDTO getMovieRequestById(Long movieId) {
        Movie movie = movieRepository.findByIdWithGenre(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim này"));
        return MovieRequestDTO.builder()
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(PosterUrlUtil.normalize(movie.getPosterUrl()))
                .genreId(movie.getGenre() != null ? movie.getGenre().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public void createMovie(MovieRequestDTO dto) {
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim này"));

        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(dto.getReleaseDate())
                .posterUrl(PosterUrlUtil.normalize(dto.getPosterUrl()))
                .genre(genre)
                .build();
        movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void updateMovie(Long movieId, MovieRequestDTO dto) {
        Movie existingMovie = movieRepository.findByIdWithGenre(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim này"));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim này"));

        if (dto.getReleaseDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày phát hành.");
        }

        existingMovie.setTitle(dto.getTitle());
        existingMovie.setDescription(dto.getDescription());
        existingMovie.setDurationMinutes(dto.getDurationMinutes());
        existingMovie.setReleaseDate(dto.getReleaseDate());
        existingMovie.setPosterUrl(PosterUrlUtil.normalize(dto.getPosterUrl()));
        existingMovie.setGenre(genre);

        movieRepository.save(existingMovie);
    }

    @Override
    @Transactional
    public void deletedMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + movieId);
        }

        long activeBookings = bookingRepository.countActiveBookingsByMovieId(movieId);
        if (activeBookings > 0) {
            throw new IllegalArgumentException(
                    "Không thể xóa phim: đã có " + activeBookings + " đơn đặt vé (đang chờ hoặc đã thanh toán). "
                            + "Vui lòng hủy/xử lý đơn trước khi xóa phim.");
        }

        // Xóa đơn đã hủy (nếu có) rồi suất chiếu — tránh lỗi FK showtimes → movies
        bookingRepository.deleteByShowtime_Movie_Id(movieId);
        showtimeRepository.deleteByMovie_Id(movieId);
        movieRepository.deleteById(movieId);
    }

    private MovieListItemDto toListItem(Movie movie) {
        return MovieListItemDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .posterUrl(PosterUrlUtil.normalize(movie.getPosterUrl()))
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .genreName(movie.getGenre() != null ? movie.getGenre().getName() : "—")
                .build();
    }
}
