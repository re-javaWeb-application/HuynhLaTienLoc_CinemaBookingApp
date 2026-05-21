package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.admin.RevenueDailyRowDto;
import com.re.cinema_manager.dto.admin.RevenueMonthlyRowDto;
import com.re.cinema_manager.dto.admin.RevenueReportDto;
import com.re.cinema_manager.repository.BookingRepository;
import com.re.cinema_manager.repository.PaymentRepository;
import com.re.cinema_manager.repository.projection.MonthlyRevenueProjection;
import com.re.cinema_manager.repository.projection.RevenueDailyProjection;
import com.re.cinema_manager.service.RevenueReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RevenueReportServiceImpl implements RevenueReportService {

    private static final DateTimeFormatter MONTH_LABEL =
            DateTimeFormatter.ofPattern("MM/yyyy", new Locale("vi", "VN"));

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public RevenueReportDto buildReport(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("Khoảng ngày báo cáo không hợp lệ.");
        }

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime toExclusive = toDate.plusDays(1).atStartOfDay();

        BigDecimal totalRevenue = paymentRepository.sumSuccessAmountBetween(from, toExclusive);
        long paidCount = paymentRepository.countSuccessPaymentsBetween(from, toExclusive);
        long cancelled = bookingRepository.countCancelledBetween(from, toExclusive);

        List<RevenueDailyRowDto> daily = paymentRepository.sumRevenueByDay(from, toExclusive).stream()
                .map(this::toDailyRow)
                .toList();

        List<RevenueMonthlyRowDto> monthly = paymentRepository.sumRevenueByMonth(from, toExclusive).stream()
                .map(this::toMonthlyRow)
                .toList();

        return RevenueReportDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalPaidBookings(paidCount)
                .cancelledBookingsInRange(cancelled)
                .dailyRows(daily)
                .monthlyRows(monthly)
                .build();
    }

    private RevenueDailyRowDto toDailyRow(RevenueDailyProjection row) {
        return RevenueDailyRowDto.builder()
                .date(row.getReportDate())
                .revenue(row.getTotalRevenue() != null ? row.getTotalRevenue() : BigDecimal.ZERO)
                .paymentCount(row.getPaymentCount() != null ? row.getPaymentCount() : 0L)
                .build();
    }

    private RevenueMonthlyRowDto toMonthlyRow(MonthlyRevenueProjection row) {
        int year = row.getReportYear() != null ? row.getReportYear() : 0;
        int month = row.getReportMonth() != null ? row.getReportMonth() : 0;
        String label = YearMonth.of(year, month).format(MONTH_LABEL);
        return RevenueMonthlyRowDto.builder()
                .year(year)
                .month(month)
                .monthLabel(label)
                .revenue(row.getTotalRevenue() != null ? row.getTotalRevenue() : BigDecimal.ZERO)
                .paymentCount(row.getPaymentCount() != null ? row.getPaymentCount() : 0L)
                .build();
    }
}
