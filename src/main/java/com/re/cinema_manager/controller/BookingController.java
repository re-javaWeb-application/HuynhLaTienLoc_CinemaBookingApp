package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.CustomerHomeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Đặt vé cho khách hàng (customer).
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final CustomerHomeService customerHomeService;

    @GetMapping("/{showtimeId}")
    public String showBookingPage(@PathVariable Long showtimeId,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "Vui lòng đăng nhập để đặt vé.");
            redirectAttributes.addFlashAttribute("openModal", "login");
            return "redirect:/home";
        }
        if (user.getRole() == Role.ADMIN) {
            redirectAttributes.addFlashAttribute("accessDeniedMessage",
                    "Tài khoản quản trị không dùng chức năng đặt vé khách.");
            return "redirect:/home";
        }

        try {
            model.addAttribute("booking", customerHomeService.getShowtimeForBooking(showtimeId));
            return "customer/booking";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/home";
        }
    }
}
