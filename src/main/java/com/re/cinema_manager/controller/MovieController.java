package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.dto.MovieRequestDTO;
import com.re.cinema_manager.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public String showMovieList(Model model) {
        model.addAttribute("movies", movieService.listMoviesForAdmin());
        return "admin/movie-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("movieDto")) {
            model.addAttribute("movieDto", new MovieRequestDTO());
        }
        model.addAttribute("genres", movieService.listGenreOptions());
        return "admin/movie-form";
    }

    @PostMapping("/create")
    public String processCreateMovie(@Valid @ModelAttribute("movieDto") MovieRequestDTO dto,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", movieService.listGenreOptions());
            model.addAttribute("validationError", "Vui lòng kiểm tra lại các trường bắt buộc.");
            return "admin/movie-form";
        }
        try {
            movieService.createMovie(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm phim mới thành công!");
            return "redirect:/admin/movies";
        } catch (Exception e) {
            model.addAttribute("genres", movieService.listGenreOptions());
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "admin/movie-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("movieDto")) {
                model.addAttribute("movieDto", movieService.getMovieRequestById(id));
            }
            if (!model.containsAttribute("movieId")) {
                model.addAttribute("movieId", id);
            }
            model.addAttribute("genres", movieService.listGenreOptions());
            return "admin/movie-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phim: " + e.getMessage());
            return "redirect:/admin/movies";
        }
    }

    @PostMapping("/edit/{id}")
    public String processUpdateMovie(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("movieDto") MovieRequestDTO dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        model.addAttribute("movieId", id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", movieService.listGenreOptions());
            model.addAttribute("validationError", "Vui lòng kiểm tra lại các trường bắt buộc.");
            return "admin/movie-form";
        }
        try {
            movieService.updateMovie(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin phim thành công!");
            return "redirect:/admin/movies";
        } catch (Exception e) {
            model.addAttribute("genres", movieService.listGenreOptions());
            model.addAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
            return "admin/movie-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            movieService.deletedMovie(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phim khỏi hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa phim: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
}
