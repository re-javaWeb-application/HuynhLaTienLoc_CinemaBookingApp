package com.re.cinema_manager.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TopMovieRevenueDto {

    private Long movieId;
    private String movieTitle;
    private String posterUrl;
    private BigDecimal totalRevenue;
    private long bookingCount;
    private int rank;
}
