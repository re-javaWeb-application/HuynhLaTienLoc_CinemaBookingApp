package com.re.cinema_manager.exception;

public class InvalidBookingRequestException extends BookingException {

    public InvalidBookingRequestException(String message) {
        super("INVALID_BOOKING_REQUEST", message);
    }
}
