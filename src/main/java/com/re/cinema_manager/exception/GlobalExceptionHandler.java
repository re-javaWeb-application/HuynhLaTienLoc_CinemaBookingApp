package com.re.cinema_manager.exception;

import com.re.cinema_manager.dto.booking.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.re.cinema_manager.controller.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ApiErrorResponse> handleSeatTaken(SeatAlreadyBookedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .takenSeats(ex.getTakenSeatNames())
                        .build()
        );
    }

    @ExceptionHandler(InvalidBookingRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalid(InvalidBookingRequestException ex) {
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ApiErrorResponse> handleBooking(BookingException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ApiErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiErrorResponse.builder()
                        .errorCode("FORBIDDEN")
                        .message(ex.getMessage())
                        .build()
        );
    }
}
