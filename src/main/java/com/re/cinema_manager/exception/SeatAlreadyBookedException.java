package com.re.cinema_manager.exception;

import java.util.List;

public class SeatAlreadyBookedException extends BookingException {

    private final List<String> takenSeatNames;

    public SeatAlreadyBookedException(List<String> takenSeatNames) {
        super("SEAT_ALREADY_BOOKED",
                "Một hoặc nhiều ghế đã được đặt: " + String.join(", ", takenSeatNames));
        this.takenSeatNames = takenSeatNames;
    }

    public List<String> getTakenSeatNames() {
        return takenSeatNames;
    }
}
