package com.re.cinema_manager.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Ghế ngồi trong phòng (bảng {@code seats}).
 */
@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(name = "uk_seat_room_name", columnNames = {"room_id", "seat_name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "seat_name", nullable = false, length = 10)
    private String seatName;
}
