package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.BookingHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.re.cinema_manager.exception.BookingException;
import com.re.cinema_manager.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingHistoryController {

    private final BookingHistoryService bookingHistoryService;
    private final BookingService bookingService;

    @GetMapping("/history")
    public String history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "Vui lòng đăng nhập để xem lịch sử vé.");
            redirectAttributes.addFlashAttribute("openModal", "login");
            return "redirect:/home";
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<?> historyPage = bookingHistoryService.getBookingHistory(user.getId(), pageable);

        model.addAttribute("historyPage", historyPage);
        model.addAttribute("currentPage", page);
        return "customer/booking-history";
    }

    @PostMapping("/history/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "Vui lòng đăng nhập.");
            redirectAttributes.addFlashAttribute("openModal", "login");
            return "redirect:/home";
        }
        try {
            bookingService.cancelBooking(user.getId(), bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy vé thành công. Ghế đã được giải phóng.");
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/booking/history";
    }
}
