package com.re.cinema_manager.model.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieOptionDto {

    private Long id;
    private String title;
    private int durationMinutes;
}
