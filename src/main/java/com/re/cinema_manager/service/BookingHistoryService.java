package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.booking.BookingHistoryItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingHistoryService {

    Page<BookingHistoryItemDto> getBookingHistory(Long userId, Pageable pageable);
}
