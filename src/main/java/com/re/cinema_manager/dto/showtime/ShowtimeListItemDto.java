package com.re.cinema_manager.dto.showtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeListItemDto {

    private Long id;
    private String movieTitle;
    private int movieDurationMinutes;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime roomFreeAt;
}
