package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.booking.BookingInvoiceDto;
import com.re.cinema_manager.dto.staff.StaffBookingDetailDto;

public interface StaffBookingService {

    StaffBookingDetailDto lookupByCode(String bookingCode);

    BookingInvoiceDto confirmCounterPayment(Long bookingId);

    BookingInvoiceDto getPrintableInvoice(Long bookingId);
}
