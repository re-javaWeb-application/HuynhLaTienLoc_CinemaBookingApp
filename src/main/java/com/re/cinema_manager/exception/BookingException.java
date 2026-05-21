package com.re.cinema_manager.exception;

import lombok.Getter;

@Getter
public class BookingException extends RuntimeException {

    private final String errorCode;

    public BookingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
