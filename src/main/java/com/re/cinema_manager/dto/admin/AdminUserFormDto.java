package com.re.cinema_manager.dto.admin;

import com.re.cinema_manager.model.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserFormDto {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập từ 3–50 ký tự")
    private String username;

    /** Tạo mới: bắt buộc (kiểm tra service). Sửa: để trống = giữ mật khẩu cũ */
    private String password;

    @NotNull(message = "Vai trò không được để trống")
    private Role role;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(max = 20)
    private String phone;
}
