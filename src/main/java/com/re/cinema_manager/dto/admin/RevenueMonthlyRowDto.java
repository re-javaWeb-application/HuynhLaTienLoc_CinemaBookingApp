package com.re.cinema_manager.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RevenueMonthlyRowDto {

    private int year;
    private int month;
    private String monthLabel;
    private BigDecimal revenue;
    private long paymentCount;
}
