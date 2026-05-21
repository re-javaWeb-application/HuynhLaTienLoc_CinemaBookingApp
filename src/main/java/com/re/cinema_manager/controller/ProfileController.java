package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.profile.ProfileViewModel;
import com.re.cinema_manager.dto.profile.UpdateProfileRequest;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ──────────────────────────────────────────────
    // GET /profile — Hiển thị trang hồ sơ
    // ──────────────────────────────────────────────
    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Bảo mật: chưa đăng nhập → redirect /home
        if (loggedInUser == null) {
            return "redirect:/home";
        }

        ProfileViewModel profile = profileService.getProfileByUserId(loggedInUser.getId());
        model.addAttribute("profile", profile);

        // Form request (nếu chưa có từ flash/redirect)
        if (!model.containsAttribute("updateRequest")) {
            UpdateProfileRequest req = new UpdateProfileRequest();
            req.setFullName(profile.getFullName());
            req.setPhone(profile.getPhone());
            model.addAttribute("updateRequest", req);
        }

        return "profile";
    }

    // ──────────────────────────────────────────────
    // POST /profile/update — Cập nhật hồ sơ
    // ──────────────────────────────────────────────
    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("updateRequest") UpdateProfileRequest request,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Bảo mật: chưa đăng nhập → redirect /home
        if (loggedInUser == null) {
            return "redirect:/home";
        }

        // Có lỗi validation → trả về form với lỗi
        if (bindingResult.hasErrors()) {
            ProfileViewModel profile = profileService.getProfileByUserId(loggedInUser.getId());
            model.addAttribute("profile", profile);
            model.addAttribute("toastType", "error");
            model.addAttribute("toastMessage", "Vui lòng kiểm tra lại thông tin.");
            return "profile";
        }

        try {
            profileService.updateProfile(loggedInUser.getId(), request);
            redirectAttributes.addFlashAttribute("toastType", "success");
            redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastType", "error");
            redirectAttributes.addFlashAttribute("toastMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
