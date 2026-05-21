package com.re.cinema_manager.dto.staff;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffBookedSeatDto {

    private String seatName;
    private String bookingCode;
    private String customerLabel;
    private String bookingStatus;
}
