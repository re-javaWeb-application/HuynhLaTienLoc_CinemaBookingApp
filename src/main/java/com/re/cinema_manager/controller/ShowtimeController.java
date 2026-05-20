package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.dto.ShowtimeRequestDTO;
import com.re.cinema_manager.model.entity.Showtime;
import com.re.cinema_manager.repository.MovieRepository;
import com.re.cinema_manager.repository.RoomRepository;
import com.re.cinema_manager.service.ShowtimeService;
import com.re.cinema_manager.service.showtime.ShowtimeConflictChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * CORE-05 — Admin quản lý suất chiếu.
 * URL: /admin/showtimes (được bảo vệ bởi {@link com.re.cinema_manager.interceptor.AdminInterceptor}).
 */
@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    /** Danh sách lịch chiếu — bước xem sau khi tạo thành công. */
    @GetMapping
    public String listShowtimes(Model model) {
        List<Showtime> showtimes = showtimeService.findAllShowtimes();
        model.addAttribute("showtimes", showtimes);
        model.addAttribute("cleanupBufferMinutes", ShowtimeConflictChecker.CLEANUP_BUFFER_MINUTES);
        return "admin/showtime-list";
    }

    /** Form tạo suất: Phim + Phòng + Giờ bắt đầu. */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("showtimeDto")) {
            model.addAttribute("showtimeDto", new ShowtimeRequestDTO());
        }
        populateDropdowns(model);
        return "admin/showtime-form";
    }

    /**
     * Xử lý POST tạo suất — gọi service (có kiểm tra xung đột phòng).
     * Thất bại → flash lỗi + quay lại form; thành công → redirect danh sách.
     */
    @PostMapping("/create")
    public String processCreate(@ModelAttribute("showtimeDto") ShowtimeRequestDTO dto,
                              RedirectAttributes redirectAttributes) {
        try {
            showtimeService.createShowtime(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo lịch chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("showtimeDto", dto);
            return "redirect:/admin/showtimes/create";
        }
        return "redirect:/admin/showtimes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("showtimeDto")) {
                Showtime showtime = showtimeService.getShowtimeById(id);
                ShowtimeRequestDTO dto = ShowtimeRequestDTO.builder()
                        .movieId(showtime.getMovie().getId())
                        .roomId(showtime.getRoom().getId())
                        .startTime(showtime.getStartTime())
                        .build();
                model.addAttribute("showtimeDto", dto);
            }
            if (!model.containsAttribute("showtimeId")) {
                model.addAttribute("showtimeId", id);
            }
            populateDropdowns(model);
            return "admin/showtime-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/showtimes";
        }
    }

    @PostMapping("/edit/{id}")
    public String processUpdate(@PathVariable Long id,
                                @ModelAttribute("showtimeDto") ShowtimeRequestDTO dto,
                                RedirectAttributes redirectAttributes) {
        try {
            showtimeService.updateShowtime(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lịch chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("showtimeDto", dto);
            redirectAttributes.addFlashAttribute("showtimeId", id);
            return "redirect:/admin/showtimes/edit/" + id;
        }
        return "redirect:/admin/showtimes";
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

    private void populateDropdowns(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
    }
}
