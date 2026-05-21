package com.re.cinema_manager.dto.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatRowView {

    private Long id;
    private String seatName;
    private boolean booked;
}
