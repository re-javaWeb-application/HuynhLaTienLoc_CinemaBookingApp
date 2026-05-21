package com.re.cinema_manager.dto.admin;

import com.re.cinema_manager.model.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserListItemDto {

    private Long id;
    private String username;
    private Role role;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private long bookingCount;
}
