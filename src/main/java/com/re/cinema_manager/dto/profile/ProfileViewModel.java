package com.re.cinema_manager.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ViewModel để truyền thông tin profile ra View (Thymeleaf).
 * Không truyền thẳng Entity, đảm bảo tách biệt tầng Presentation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileViewModel {

    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime joinDate;

    /** Chữ cái đầu của fullName để hiển thị avatar */
    public String getAvatarInitial() {
        if (fullName != null && !fullName.isBlank()) {
            return String.valueOf(fullName.trim().charAt(0)).toUpperCase();
        }
        if (username != null && !username.isBlank()) {
            return String.valueOf(username.charAt(0)).toUpperCase();
        }
        return "U";
    }
}
