package com.re.cinema_manager.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RevenueDailyRowDto {

    private LocalDate date;
    private BigDecimal revenue;
    private long paymentCount;
}
