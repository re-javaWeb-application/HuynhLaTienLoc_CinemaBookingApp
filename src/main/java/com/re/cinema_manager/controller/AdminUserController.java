package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.admin.AdminUserFormDto;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.service.AdminUserService;
import com.re.cinema_manager.util.ValidationRedirectHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", adminUserService.findAllUsers());
        return "admin/user-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("userForm", new AdminUserFormDto());
        model.addAttribute("roles", Role.values());
        model.addAttribute("editMode", false);
        return "admin/user-form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userForm") AdminUserFormDto form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin.");
            return "redirect:/admin/users/create";
        }
        try {
            adminUserService.createUser(form);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo tài khoản " + form.getUsername());
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("userForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("userForm", adminUserService.getFormForEdit(id));
            model.addAttribute("userId", id);
            model.addAttribute("roles", Role.values());
            model.addAttribute("editMode", true);
            return "admin/user-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userForm") AdminUserFormDto form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin.");
            redirectAttributes.addFlashAttribute("userForm", form);
            return "redirect:/admin/users/edit/" + id;
        }
        try {
            adminUserService.updateUser(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật tài khoản " + form.getUsername());
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("userForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User current = (User) session.getAttribute("loggedInUser");
        Long currentId = current != null ? current.getId() : null;
        try {
            adminUserService.deleteUser(id, currentId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tài khoản.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
