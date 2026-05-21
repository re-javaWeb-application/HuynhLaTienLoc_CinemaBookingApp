package com.re.cinema_manager.dto.showtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomOptionDto {

    private Long id;
    private String roomName;
    private int capacity;
}
