package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.model.dto.ShowtimeRequestDTO;
import com.re.cinema_manager.model.entity.Movie;
import com.re.cinema_manager.model.entity.Room;
import com.re.cinema_manager.model.entity.Showtime;
import com.re.cinema_manager.repository.MovieRepository;
import com.re.cinema_manager.repository.RoomRepository;
import com.re.cinema_manager.repository.ShowtimeRepository;
import com.re.cinema_manager.service.ShowtimeService;
import com.re.cinema_manager.service.showtime.ShowtimeConflictChecker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai CORE-05 — Quản lý suất chiếu.
 * <p>
 * Luồng nghiệp vụ (khớp sequence diagram):
 * <ol>
 *   <li>Admin chọn Phim + Phòng + Giờ bắt đầu</li>
 *   <li>Hệ thống hỏi Lưu trữ: các suất hiện có của phòng đó</li>
 *   <li>Kiểm tra xung đột (phim dài + 15 phút dọn phòng)</li>
 *   <li>Nếu trùng → báo lỗi; nếu trống → ghi nhận suất mới</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<Showtime> findAllShowtimes() {
        return showtimeRepository.findAllWithMovieAndRoom();
    }

    @Override
    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findByIdWithMovieAndRoom(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu với ID: " + id));
    }

    @Override
    @Transactional
    public Showtime createShowtime(ShowtimeRequestDTO dto) {
        validateRequest(dto);

        Movie movie = loadMovie(dto.getMovieId());
        Room room = loadRoom(dto.getRoomId());

        // --- CORE-05: Kiểm tra xung đột phòng trước khi ghi DB ---
        assertNoRoomConflict(room.getId(), dto.getStartTime(), movie.getDurationMinutes(), null);

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(dto.getStartTime())
                .build();

        return showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public Showtime updateShowtime(Long id, ShowtimeRequestDTO dto) {
        validateRequest(dto);

        Showtime existing = getShowtimeById(id);
        Movie movie = loadMovie(dto.getMovieId());
        Room room = loadRoom(dto.getRoomId());

        // Khi sửa: loại trừ chính suất đang sửa khỏi danh sách so sánh
        assertNoRoomConflict(room.getId(), dto.getStartTime(), movie.getDurationMinutes(), id);

        existing.setMovie(movie);
        existing.setRoom(room);
        existing.setStartTime(dto.getStartTime());

        return showtimeRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy suất chiếu với ID: " + id);
        }
        showtimeRepository.deleteById(id);
    }

    // =========================================================================================
    // CORE-05 — Kiểm tra xung đột phòng (private helpers)
    // =========================================================================================

    /**
     * Duyệt mọi suất đã có trong cùng phòng; nếu bất kỳ suất nào giao thời gian chiếm phòng
     * với suất mới → ném {@link IllegalArgumentException} (Controller bắt và hiển thị cho Admin).
     *
     * @param roomId              phòng Admin chọn
     * @param proposedStart       giờ bắt đầu suất mới
     * @param proposedDurationMin thời lượng phim mới (phút)
     * @param excludeShowtimeId   null khi tạo mới; id suất hiện tại khi sửa
     */
    private void assertNoRoomConflict(
            Long roomId,
            LocalDateTime proposedStart,
            int proposedDurationMin,
            Long excludeShowtimeId) {

        // Bước 1 (sequence diagram): "Kiểm tra xem phòng đó có đang bận không"
        // → Lấy toàn bộ lịch chiếu hiện có của phòng từ Lưu trữ (DB)
        List<Showtime> schedulesInRoom = showtimeRepository.findByRoomIdWithMovieAndRoom(roomId);

        // Bước 2: So sánh suất ĐỀ XUẤT với từng suất ĐÃ CÓ
        for (Showtime existing : schedulesInRoom) {
            Optional<String> conflictMessage = ShowtimeConflictChecker.findConflictWithExisting(
                    proposedStart,
                    proposedDurationMin,
                    existing,
                    excludeShowtimeId);

            // Bước 3 (nhánh alt — phòng bận): "Thông báo trùng lịch, yêu cầu chọn giờ khác"
            if (conflictMessage.isPresent()) {
                throw new IllegalArgumentException(conflictMessage.get());
            }
        }
        // Bước 3 (nhánh alt — phòng trống): không ném lỗi → caller được phép save
    }

    private void validateRequest(ShowtimeRequestDTO dto) {
        if (dto.getMovieId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phim.");
        }
        if (dto.getRoomId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phòng chiếu.");
        }
        if (dto.getStartTime() == null) {
            throw new IllegalArgumentException("Vui lòng chọn giờ bắt đầu chiếu.");
        }
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Giờ chiếu phải ở tương lai (không được chọn quá khứ).");
        }
    }

    private Movie loadMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phim với ID: " + movieId));
    }

    private Room loadRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với ID: " + roomId));
    }
}
