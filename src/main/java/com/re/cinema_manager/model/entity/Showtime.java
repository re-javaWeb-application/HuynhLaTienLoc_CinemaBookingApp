package com.re.cinema_manager.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Suất chiếu (CORE-05): Admin chọn Phim + Phòng + Giờ bắt đầu.
 * Thời gian phòng bị "chiếm" = thời lượng phim + thời gian dọn phòng (xem {@link com.re.cinema_manager.service.showtime.ShowtimeConflictChecker}).
 */
@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Giờ bắt đầu chiếu (không bao gồm thời gian dọn phòng trước đó). */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
}
