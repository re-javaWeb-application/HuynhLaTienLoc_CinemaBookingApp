package com.re.cinema_manager.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Phòng chiếu vật lý trong rạp (CORE-05).
 * Mỗi suất chiếu gắn với đúng một phòng; logic xung đột chỉ so sánh các suất trong cùng phòng.
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, unique = true, length = 50)
    private String roomName;

    @Column(name = "capacity", nullable = false)
    private int capacity;
}
