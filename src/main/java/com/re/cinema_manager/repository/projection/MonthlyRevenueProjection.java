package com.re.cinema_manager.repository.projection;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {

    Integer getReportYear();

    Integer getReportMonth();

    BigDecimal getTotalRevenue();

    Long getPaymentCount();
}
