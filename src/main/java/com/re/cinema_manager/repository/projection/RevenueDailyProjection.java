package com.re.cinema_manager.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueDailyProjection {

    LocalDate getReportDate();

    BigDecimal getTotalRevenue();

    Long getPaymentCount();
}
