package com.re.cinema_manager.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequest {

    @NotNull
    private Long showtimeId;

    @NotEmpty
    private List<Long> seatIds;

    private String paymentMethod = "CASH";
}
