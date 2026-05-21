package com.re.cinema_manager.model.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieListItemDto {

    private Long id;
    private String title;
    private String description;
    private String posterUrl;
    private int durationMinutes;
    private LocalDate releaseDate;
    private String genreName;
}
