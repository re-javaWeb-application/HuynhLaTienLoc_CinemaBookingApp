package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Payment;
import com.re.cinema_manager.repository.projection.MonthlyRevenueProjection;
import com.re.cinema_manager.repository.projection.RevenueDailyProjection;
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
}
