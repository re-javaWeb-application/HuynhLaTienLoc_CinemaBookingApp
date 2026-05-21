package com.re.cinema_manager.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RevenueReportDto {

    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
    private long totalPaidBookings;
    private long cancelledBookingsInRange;
    private List<RevenueDailyRowDto> dailyRows;
    private List<RevenueMonthlyRowDto> monthlyRows;
    /** Top 5 phim — tính bằng SQL GROUP BY, không cộng dồn trong Java */
    private List<TopMovieRevenueDto> topMovies;
}
