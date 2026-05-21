package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.booking.CreateBookingRequest;
import com.re.cinema_manager.exception.BookingException;
import com.re.cinema_manager.exception.SeatAlreadyBookedException;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.BookingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Đặt vé cho khách hàng (CORE-06) — chọn ghế + thanh toán + hóa đơn.
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{showtimeId}")
    public String showSeatSelection(@PathVariable Long showtimeId,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        User user = requireLoggedInCustomer(session, redirectAttributes);
        if (user == null) {
            return "redirect:/home";
        }

        try {
            model.addAttribute("seatView", bookingService.getSeatSelection(showtimeId));
            return "customer/booking-seats";
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/home";
        }
    }

    @PostMapping("/{showtimeId}/pay")
    public String pay(@PathVariable Long showtimeId,
                      @RequestParam(value = "seatIds", required = false) List<Long> seatIds,
                      @RequestParam(defaultValue = "CASH") String paymentMethod,
                      HttpSession session,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        User user = requireLoggedInCustomer(session, redirectAttributes);
        if (user == null) {
            return "redirect:/home";
        }

        if (seatIds == null || seatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ít nhất một ghế.");
            return "redirect:/booking/" + showtimeId;
        }

        CreateBookingRequest request = new CreateBookingRequest();
        request.setShowtimeId(showtimeId);
        request.setSeatIds(seatIds);
        request.setPaymentMethod(paymentMethod);

        try {
            model.addAttribute("invoice", bookingService.createBooking(user.getId(), request));
            return "customer/booking-invoice";
        } catch (SeatAlreadyBookedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/" + showtimeId;
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/" + showtimeId;
        }
    }

    @GetMapping("/invoice/{bookingId}")
    public String viewInvoice(@PathVariable Long bookingId,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        User user = requireLoggedInCustomer(session, redirectAttributes);
        if (user == null) {
            return "redirect:/home";
        }
        try {
            model.addAttribute("invoice", bookingService.getInvoiceByIdAndUser(bookingId, user.getId()));
            return "customer/booking-invoice";
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/history";
        }
    }

    private User requireLoggedInCustomer(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "Vui lòng đăng nhập để đặt vé.");
            redirectAttributes.addFlashAttribute("openModal", "login");
            return null;
        }
        return user;
    }

}
