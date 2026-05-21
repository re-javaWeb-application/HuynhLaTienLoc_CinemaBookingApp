package com.re.cinema_manager.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "Vui lòng nhập tên phim")
    @Size(max = 255, message = "Tên phim tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Vui lòng nhập mô tả")
    @Size(max = 5000, message = "Mô tả quá dài")
    private String description;

    @Min(value = 1, message = "Thời lượng phải lớn hơn 0 phút")
    private int durationMinutes;

    @NotNull(message = "Vui lòng chọn ngày phát hành")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @NotBlank(message = "Vui lòng nhập URL poster")
    @Size(max = 255, message = "URL poster tối đa 255 ký tự")
    private String posterUrl;

    @NotNull(message = "Vui lòng chọn thể loại")
    private Long genreId;
}
