package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Payment;
import com.re.cinema_manager.repository.projection.MonthlyRevenueProjection;
import com.re.cinema_manager.repository.projection.RevenueDailyProjection;
import com.re.cinema_manager.repository.projection.TopMovieRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = """
            SELECT DATE(p.paid_at) AS reportDate,
                   COALESCE(SUM(p.amount), 0) AS totalRevenue,
                   COUNT(p.id) AS paymentCount
            FROM payments p
            WHERE p.status = 'SUCCESS'
              AND p.paid_at IS NOT NULL
              AND p.paid_at >= :from
              AND p.paid_at < :to
            GROUP BY DATE(p.paid_at)
            ORDER BY reportDate DESC
            """, nativeQuery = true)
    List<RevenueDailyProjection> sumRevenueByDay(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT YEAR(p.paid_at) AS reportYear,
                   MONTH(p.paid_at) AS reportMonth,
                   COALESCE(SUM(p.amount), 0) AS totalRevenue,
                   COUNT(p.id) AS paymentCount
            FROM payments p
            WHERE p.status = 'SUCCESS'
              AND p.paid_at IS NOT NULL
              AND p.paid_at >= :from
              AND p.paid_at < :to
            GROUP BY YEAR(p.paid_at), MONTH(p.paid_at)
            ORDER BY reportYear DESC, reportMonth DESC
            """, nativeQuery = true)
    List<MonthlyRevenueProjection> sumRevenueByMonth(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM Payment p
            WHERE p.status = com.re.cinema_manager.model.entity.PaymentStatus.SUCCESS
              AND p.paidAt IS NOT NULL
              AND p.paidAt >= :from AND p.paidAt < :to
            """)
    BigDecimal sumSuccessAmountBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(p) FROM Payment p
            WHERE p.status = com.re.cinema_manager.model.entity.PaymentStatus.SUCCESS
              AND p.paidAt IS NOT NULL
              AND p.paidAt >= :from AND p.paidAt < :to
            """)
    long countSuccessPaymentsBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /**
     * Top 5 phim doanh thu cao nhất — JOIN + GROUP BY + HAVING + ORDER BY (Hướng 4).
     */
    @Query(value = """
            SELECT m.id AS movieId,
                   m.title AS movieTitle,
                   m.poster_url AS posterUrl,
                   COALESCE(SUM(p.amount), 0) AS totalRevenue,
                   COUNT(DISTINCT b.id) AS bookingCount
            FROM payments p
            INNER JOIN bookings b ON b.id = p.booking_id AND b.status = 'PAID'
            INNER JOIN showtimes st ON st.id = b.showtime_id
            INNER JOIN movies m ON m.id = st.movie_id
            WHERE p.status = 'SUCCESS'
              AND p.paid_at IS NOT NULL
              AND p.paid_at >= :from
              AND p.paid_at < :to
            GROUP BY m.id, m.title, m.poster_url
            HAVING SUM(p.amount) > 0
            ORDER BY totalRevenue DESC
            LIMIT 5
            """, nativeQuery = true)
    List<TopMovieRevenueProjection> findTopMoviesByRevenue(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /** Doanh thu theo tháng — thứ tự tăng dần (phục vụ biểu đồ). */
    @Query(value = """
            SELECT YEAR(p.paid_at) AS reportYear,
                   MONTH(p.paid_at) AS reportMonth,
                   COALESCE(SUM(p.amount), 0) AS totalRevenue,
                   COUNT(p.id) AS paymentCount
            FROM payments p
            WHERE p.status = 'SUCCESS'
              AND p.paid_at IS NOT NULL
              AND p.paid_at >= :from
              AND p.paid_at < :to
            GROUP BY YEAR(p.paid_at), MONTH(p.paid_at)
            ORDER BY reportYear ASC, reportMonth ASC
            """, nativeQuery = true)
    List<MonthlyRevenueProjection> sumRevenueByMonthAsc(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
