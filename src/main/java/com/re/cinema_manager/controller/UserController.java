package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.auth.LoginDto;
import com.re.cinema_manager.dto.auth.RegisterRequestDTO;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.service.CustomerHomeService;
import com.re.cinema_manager.service.UserService;
import com.re.cinema_manager.util.ValidationRedirectHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomerHomeService customerHomeService;

    @ModelAttribute("registerRequest")
    public RegisterRequestDTO registerRequest() {
        return new RegisterRequestDTO();
    }

    @GetMapping({ "/register" })
    public String showRegisterPage() {
        return "redirect:/home";
    }

    @PostMapping({ "/register" })
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequestDTO registerRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("openModal", "register");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            ValidationRedirectHelper.flashFieldErrors(redirectAttributes, bindingResult, "registerFieldErrors");
            redirectAttributes.addFlashAttribute("registerError", "Vui lòng kiểm tra lại thông tin đăng ký.");
            return "redirect:/home";
        }
        try {
            userService.Register(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Mời bạn đăng nhập.");
            redirectAttributes.addFlashAttribute("openModal", "login");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            redirectAttributes.addFlashAttribute("openModal", "register");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("registerError",
                    "Email hoặc số điện thoại đã được sử dụng. Vui lòng thử lại.");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
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
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công! Chào mừng bạn trở lại.");
            if (user.getRole() == Role.STAFF) {
                return "redirect:/staff/dashboard";
            }
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
        if (user != null && user.getRole() == Role.STAFF) {
            return "redirect:/staff";
        }

        boolean customerView = user == null || user.getRole() != Role.ADMIN;
        model.addAttribute("customerView", customerView);
        if (customerView) {
            model.addAttribute("genreSections", customerHomeService.getUpcomingMoviesByGenre());
        }
        return "home";
    }


}
