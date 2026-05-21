package com.re.cinema_manager.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiErrorResponse {

    private String errorCode;
    private String message;
    private List<String> takenSeats;
}
