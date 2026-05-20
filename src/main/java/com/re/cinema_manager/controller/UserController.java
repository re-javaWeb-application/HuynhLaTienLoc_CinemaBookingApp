package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.dto.LoginDto;
import com.re.cinema_manager.model.dto.RegisterRequestDTO;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.service.CustomerHomeService;
import com.re.cinema_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomerHomeService customerHomeService;

    @GetMapping({ "/register" })
    public String showRegisterPage() {
        return "redirect:/home";
    }

    @PostMapping({ "/register" })
    public String register(RegisterRequestDTO registerRequestDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.Register(registerRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Mời bạn đăng nhập.");
            redirectAttributes.addFlashAttribute("openModal", "login");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            redirectAttributes.addFlashAttribute("openModal", "register");
        } catch (DataIntegrityViolationException e) {
            // Email hoặc số điện thoại đã tồn tại trong DB
            redirectAttributes.addFlashAttribute("registerError",
                    "Email hoặc số điện thoại đã được sử dụng. Vui lòng thử lại.");
            redirectAttributes.addFlashAttribute("openModal", "register");
        }
        return "redirect:/home";
    }

    @GetMapping({ "/login" })
    public String showLoginPage() {
        return "redirect:/home";
    }

    @PostMapping({ "/login" })
    public String login(LoginDto loginDto, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            User user = userService.Login(loginDto);
            // Lưu thông tin user vào Session
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công! Chào mừng bạ trở lại.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
            redirectAttributes.addFlashAttribute("openModal", "login");
        }
        return "redirect:/home";
    }

    @GetMapping({ "/logout" })
    public String logout(HttpSession session) {
        // Xóa toàn bộ session
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping({ "/home", "/" })
    public String showHomePage(HttpSession session, Model model) {
        String accessDenied = (String) session.getAttribute("accessDeniedMessage");
        if (accessDenied != null) {
            model.addAttribute("accessDeniedMessage", accessDenied);
            session.removeAttribute("accessDeniedMessage");
        }

        User user = (User) session.getAttribute("loggedInUser");
        boolean customerView = user == null || user.getRole() != Role.ADMIN;
        model.addAttribute("customerView", customerView);
        if (customerView) {
            model.addAttribute("genreSections", customerHomeService.getUpcomingMoviesByGenre());
        }
        return "home";
    }


}
