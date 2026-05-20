package com.re.cinema_manager.service.showtime;

import com.re.cinema_manager.model.entity.Showtime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * CORE-05 — Kiểm tra xung đột lịch chiếu trong cùng một phòng.
 * <p>
 * Quy tắc nghiệp vụ (theo sequence diagram & yêu cầu đề bài):
 * <ul>
 *   <li>Một phòng tại một thời điểm không thể chiếu 2 phim.</li>
 *   <li>Sau khi phim kết thúc, cần thêm {@link #CLEANUP_BUFFER_MINUTES} phút dọn phòng
 *       trước khi suất tiếp theo được phép bắt đầu.</li>
 * </ul>
 * <p>
 * Ví dụ: Suất A — Phòng 1, bắt đầu 08:00, phim dài 120 phút.
 * <ul>
 *   <li>Phim kết thúc lúc 10:00</li>
 *   <li>Phòng rảnh từ 10:15 (10:00 + 15 phút dọn)</li>
 *   <li>Suất B cùng phòng chỉ được bắt đầu từ 10:15 trở đi</li>
 * </ul>
 */
public final class ShowtimeConflictChecker {

    /**
     * Thời gian dọn phòng sau khi phim kết thúc (phút).
     * Đây là khoảng "đệm" bắt buộc giữa hai suất liên tiếp trong cùng phòng.
     */
    public static final int CLEANUP_BUFFER_MINUTES = 15;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ShowtimeConflictChecker() {
        // Utility class — không khởi tạo
    }

    /**
     * Tính thời điểm phòng được giải phóng hoàn toàn sau một suất chiếu.
     * <p>
     * Công thức: {@code startTime + durationMinutes + CLEANUP_BUFFER_MINUTES}
     *
     * @param startTime       giờ bắt đầu chiếu
     * @param durationMinutes thời lượng phim (phút), lấy từ bảng {@code movies}
     * @return thời điểm sớm nhất mà suất khác có thể bắt đầu trong cùng phòng
     */
    public static LocalDateTime computeRoomFreeAt(LocalDateTime startTime, int durationMinutes) {
        return startTime
                .plusMinutes(durationMinutes)
                .plusMinutes(CLEANUP_BUFFER_MINUTES);
    }

    /**
     * Khoảng thời gian phòng bị chiếm bởi một suất (dùng để so sánh giao nhau).
     * <p>
     * Khoảng nửa mở: {@code [startTime, roomFreeAt)} — suất mới bắt đầu đúng lúc {@code roomFreeAt}
     * của suất cũ thì KHÔNG bị coi là trùng (đúng ví dụ 10:15).
     *
     * @param startTime       giờ bắt đầu suất đang xét
     * @param durationMinutes thời lượng phim tương ứng
     * @return mảng 2 phần tử: {@code [0] = start, [1] = roomFreeAt (exclusive end)}
     */
    public static LocalDateTime[] occupiedWindow(LocalDateTime startTime, int durationMinutes) {
        LocalDateTime freeAt = computeRoomFreeAt(startTime, durationMinutes);
        return new LocalDateTime[]{startTime, freeAt};
    }

    /**
     * Kiểm tra hai khoảng thời gian chiếm phòng có giao nhau hay không.
     * <p>
     * Hai khoảng {@code [startA, endA)} và {@code [startB, endB)} giao nhau khi:
     * {@code startA < endB AND endA > startB}
     * <p>
     * Điều kiện này bao quát mọi trường hợp: trùng hoàn toàn, lồng nhau, chỉ đè một phần đầu/cuối.
     *
     * @param startA bắt đầu chiếm phòng suất A
     * @param endA   thời điểm phòng rảnh sau suất A (exclusive)
     * @param startB bắt đầu chiếm phòng suất B
     * @param endB   thời điểm phòng rảnh sau suất B (exclusive)
     * @return {@code true} nếu không thể đặt cả hai suất cùng phòng
     */
    public static boolean windowsOverlap(
            LocalDateTime startA, LocalDateTime endA,
            LocalDateTime startB, LocalDateTime endB) {
        return startA.isBefore(endB) && endA.isAfter(startB);
    }

    /**
     * So sánh suất chiếu MỚI (đang tạo/sửa) với một suất ĐÃ CÓ trong DB.
     *
     * @param newStart              giờ bắt đầu suất mới
     * @param newDurationMinutes    thời lượng phim của suất mới
     * @param existing              suất đã lưu (phải có {@code movie} đã load để lấy duration)
     * @param excludeShowtimeId     khi sửa: bỏ qua chính suất đang sửa; khi tạo: truyền {@code null}
     * @return {@link Optional} chứa thông báo lỗi tiếng Việt nếu trùng lịch; rỗng nếu hợp lệ
     */
    public static Optional<String> findConflictWithExisting(
            LocalDateTime newStart,
            int newDurationMinutes,
            Showtime existing,
            Long excludeShowtimeId) {

        // Bước 1: Bỏ qua chính bản ghi đang cập nhật (tránh tự so với chính mình)
        if (excludeShowtimeId != null && excludeShowtimeId.equals(existing.getId())) {
            return Optional.empty();
        }

        // Bước 2: Tính khoảng chiếm phòng của suất MỚI
        LocalDateTime[] newWindow = occupiedWindow(newStart, newDurationMinutes);
        LocalDateTime newOccupiedStart = newWindow[0];
        LocalDateTime newRoomFreeAt = newWindow[1];

        // Bước 3: Tính khoảng chiếm phòng của suất ĐÃ CÓ (cùng phòng)
        int existingDuration = existing.getMovie().getDurationMinutes();
        LocalDateTime existingStart = existing.getStartTime();
        LocalDateTime existingRoomFreeAt = computeRoomFreeAt(existingStart, existingDuration);

        // Bước 4: Áp dụng công thức giao khoảng — nếu true => phòng bận, từ chối tạo lịch
        if (windowsOverlap(newOccupiedStart, newRoomFreeAt, existingStart, existingRoomFreeAt)) {
            String movieTitle = existing.getMovie().getTitle();
            String roomName = existing.getRoom().getRoomName();
            String existingStartStr = existingStart.format(DISPLAY_FORMAT);
            String freeAtStr = existingRoomFreeAt.format(DISPLAY_FORMAT);

            return Optional.of(String.format(
                    "Trùng lịch phòng \"%s\": đã có suất \"%s\" bắt đầu lúc %s. "
                            + "Phòng chỉ rảnh từ %s trở đi (đã tính %d phút dọn phòng). "
                            + "Vui lòng chọn giờ chiếu khác.",
                    roomName,
                    movieTitle,
                    existingStartStr,
                    freeAtStr,
                    CLEANUP_BUFFER_MINUTES));
        }

        return Optional.empty();
    }
}
