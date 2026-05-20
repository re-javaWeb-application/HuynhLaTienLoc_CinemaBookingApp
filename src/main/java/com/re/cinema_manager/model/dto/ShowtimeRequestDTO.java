package com.re.cinema_manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Dữ liệu form Admin tạo/sửa suất chiếu (CORE-05).
 * Chỉ nhận ID phim/phòng + giờ bắt đầu — không nhận cả entity JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeRequestDTO {

    private Long movieId;

    private Long roomId;

    /**
     * Giờ bắt đầu chiếu từ input HTML {@code datetime-local}.
     * Pattern khớp giá trị gửi lên: {@code 2026-05-20T08:00}
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;
}
