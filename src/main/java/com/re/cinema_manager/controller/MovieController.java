package com.re.cinema_manager.controller;

import com.re.cinema_manager.model.dto.MovieRequestDTO;
import com.re.cinema_manager.model.entity.Genre;
import com.re.cinema_manager.model.entity.Movie;
import com.re.cinema_manager.repository.GenreRepository;
import com.re.cinema_manager.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor // Tự động inject MovieService và GenreRepository qua Constructor
public class MovieController {

    private final MovieService movieService;
    private final GenreRepository genreRepository;

    // 1. LUỒNG XEM: Hiển thị danh sách toàn bộ phim
    @GetMapping
    public String showMovieList(Model model) {
        List<Movie> movies = movieService.showAllMovie();
        model.addAttribute("movies", movies); // Đẩy danh sách phim ra cho Thymeleaf vẽ bảng
        return "admin/movie-list"; // Trỏ tới file templates/admin/movie-list.html
    }

    // 2. LUỒNG THÊM (Nhịp 1): Hiển thị form trống để Admin nhập liệu
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("movieDto")) {
            model.addAttribute("movieDto", new MovieRequestDTO());
        }

        // Lấy danh sách thể loại phim (Seed data) để đổ vào thanh cuộn Dropdown select
        List<Genre> genres = genreRepository.findAll();
        model.addAttribute("genres", genres);

        return "admin/movie-form"; // Trỏ tới file templates/admin/movie-form.html
    }

    // 2. LUỒNG THÊM (Nhịp 2): Xử lý hứng dữ liệu khi Admin bấm nút "LƯU"
    @PostMapping("/create")
    public String processCreateMovie(@ModelAttribute("movieDto") MovieRequestDTO dto,
                                     RedirectAttributes redirectAttributes) {
        try {
            movieService.createMovie(dto);
            // FlashAttribute giúp truyền thông báo dạng popup ngắn sang trang tiếp theo (chỉ xuất hiện 1 lần)
            redirectAttributes.addFlashAttribute("successMessage", "Thêm phim mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("movieDto", dto);
            return "redirect:/admin/movies/create";
        }
        return "redirect:/admin/movies"; // Thành công thì chuyển hướng về trang danh sách
    }

    // 3. LUỒNG SỬA (Nhịp 1): Lấy dữ liệu cũ ra và hiển thị lên Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("movieDto")) {
                Movie movie = movieService.getMovieById(id);
                MovieRequestDTO dto = MovieRequestDTO.builder()
                        .title(movie.getTitle())
                        .description(movie.getDescription())
                        .durationMinutes(movie.getDurationMinutes())
                        .releaseDate(movie.getReleaseDate())
                        .posterUrl(movie.getPosterUrl())
                        .genreId(movie.getGenre() != null ? movie.getGenre().getId() : null)
                        .build();
                model.addAttribute("movieDto", dto);
            }
            if (!model.containsAttribute("movieId")) {
                model.addAttribute("movieId", id);
            }

            List<Genre> genres = genreRepository.findAll();
            model.addAttribute("genres", genres);

            return "admin/movie-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phim: " + e.getMessage());
            return "redirect:/admin/movies";
        }
    }

    // 3. LUỒNG SỬA (Nhịp 2): Xử lý lưu thông tin mới sau khi chỉnh sửa
    @PostMapping("/edit/{id}")
    public String processUpdateMovie(@PathVariable("id") Long id,
                                     @ModelAttribute("movieDto") MovieRequestDTO dto,
                                     RedirectAttributes redirectAttributes) {
        try {
            movieService.updateMovie(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin phim thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("movieDto", dto);
            redirectAttributes.addFlashAttribute("movieId", id);
            return "redirect:/admin/movies/edit/" + id;
        }
        return "redirect:/admin/movies";
    }

    // 4. LUỒNG XÓA: Xử lý xóa phim theo ID
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