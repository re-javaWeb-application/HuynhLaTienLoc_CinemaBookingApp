package com.re.cinema_manager.repository.projection;

import java.math.BigDecimal;

/**
 * Kết quả GROUP BY từ SQL — Top phim theo doanh thu (Hướng 4).
 */
public interface TopMovieRevenueProjection {

    Long getMovieId();

    String getMovieTitle();

    String getPosterUrl();

    BigDecimal getTotalRevenue();

    Long getBookingCount();
}
