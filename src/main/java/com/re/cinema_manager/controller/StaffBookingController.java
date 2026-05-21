package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.booking.BookingInvoiceDto;
import com.re.cinema_manager.dto.staff.StaffBookingDetailDto;
import com.re.cinema_manager.exception.BookingException;
import com.re.cinema_manager.service.StaffBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Quầy nhân viên: tra mã vé, xác nhận thanh toán, in vé.
 * Chỉ STAFF — bảo vệ bởi {@link com.re.cinema_manager.interceptor.StaffInterceptor}.
 */
@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffBookingController {

    private final StaffBookingService staffBookingService;

    @GetMapping
    public String staffHome() {
        return "redirect:/staff/counter";
    }

    @GetMapping("/counter")
    public String counterPage() {
        return "staff/counter";
    }

    @GetMapping("/counter/search")
    public String search(@RequestParam String code, Model model, RedirectAttributes redirectAttributes) {
        try {
            StaffBookingDetailDto detail = staffBookingService.lookupByCode(code);
            model.addAttribute("detail", detail);
            return "staff/counter-detail";
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/staff/counter";
        }
    }

    @PostMapping("/counter/{bookingId}/confirm")
    public String confirmPayment(@PathVariable Long bookingId,
                                 RedirectAttributes redirectAttributes) {
        try {
            BookingInvoiceDto invoice = staffBookingService.confirmCounterPayment(bookingId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Xác nhận thanh toán thành công cho " + invoice.getBookingCode());
            redirectAttributes.addFlashAttribute("confirmedBookingId", bookingId);
            return "redirect:/staff/counter/" + bookingId + "/print";
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/staff/counter/search?code=BK-" + bookingId;
        }
    }

    @GetMapping("/counter/{bookingId}/print")
    public String printTicket(@PathVariable Long bookingId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("invoice", staffBookingService.getPrintableInvoice(bookingId));
            model.addAttribute("printMode", true);
            return "staff/ticket-print";
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/staff/counter";
        }
    }

    @GetMapping("/counter/lookup")
    public String quickLookup(@RequestParam String code) {
        return "redirect:/staff/counter/search?code=" + code;
    }
}
