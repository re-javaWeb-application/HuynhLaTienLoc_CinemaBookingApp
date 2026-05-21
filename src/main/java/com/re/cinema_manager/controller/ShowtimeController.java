package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.dto.ShowtimeRequestDTO;
import com.re.cinema_manager.service.ShowtimeService;
import com.re.cinema_manager.service.showtime.ShowtimeConflictChecker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public String listShowtimes(Model model) {
        model.addAttribute("showtimes", showtimeService.listShowtimesForAdmin());
        model.addAttribute("cleanupBufferMinutes", ShowtimeConflictChecker.CLEANUP_BUFFER_MINUTES);
        return "admin/showtime-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("showtimeDto")) {
            model.addAttribute("showtimeDto", new ShowtimeRequestDTO());
        }
        populateFormOptions(model);
        return "admin/showtime-form";
    }

    @PostMapping("/create")
    public String processCreate(@Valid @ModelAttribute("showtimeDto") ShowtimeRequestDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormOptions(model);
            model.addAttribute("validationError", "Vui lòng kiểm tra lại các trường bắt buộc.");
            return "admin/showtime-form";
        }
        try {
            showtimeService.createShowtime(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo lịch chiếu thành công!");
            return "redirect:/admin/showtimes";
        } catch (Exception e) {
            populateFormOptions(model);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/showtime-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("showtimeDto")) {
                model.addAttribute("showtimeDto", showtimeService.getShowtimeRequestById(id));
            }
            if (!model.containsAttribute("showtimeId")) {
                model.addAttribute("showtimeId", id);
            }
            populateFormOptions(model);
            return "admin/showtime-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/showtimes";
        }
    }

    @PostMapping("/edit/{id}")
    public String processUpdate(@PathVariable Long id,
                                @Valid @ModelAttribute("showtimeDto") ShowtimeRequestDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        model.addAttribute("showtimeId", id);
        if (bindingResult.hasErrors()) {
            populateFormOptions(model);
            model.addAttribute("validationError", "Vui lòng kiểm tra lại các trường bắt buộc.");
            return "admin/showtime-form";
        }
        try {
            showtimeService.updateShowtime(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lịch chiếu thành công!");
            return "redirect:/admin/showtimes";
        } catch (Exception e) {
            populateFormOptions(model);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/showtime-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            showtimeService.deleteShowtime(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa suất chiếu!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/showtimes";
    }

    private void populateFormOptions(Model model) {
        model.addAttribute("movies", showtimeService.listMovieOptions());
        model.addAttribute("rooms", showtimeService.listRoomOptions());
    }
}
