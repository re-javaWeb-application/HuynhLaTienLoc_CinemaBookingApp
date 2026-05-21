package com.re.cinema_manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Phim sắp chiếu — hiển thị cho khách (customer). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingMovieView {

    private Long showtimeId;
    private Long movieId;
    private String title;
    private String description;
    private String posterUrl;
    private int durationMinutes;
    private Long genreId;
    private String genreName;
    private LocalDateTime startTime;
    private String roomName;
    /** CORE-08: true khi mọi ghế trong phòng đã có vé (không tính booking CANCELLED). */
    private boolean soldOut;
    private int totalSeats;
    private int bookedSeats;
}
