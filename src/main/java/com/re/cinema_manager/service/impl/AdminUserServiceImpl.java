package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.admin.AdminUserFormDto;
import com.re.cinema_manager.dto.admin.AdminUserListItemDto;
import com.re.cinema_manager.model.entity.Role;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.UserProfile;
import com.re.cinema_manager.repository.BookingRepository;
import com.re.cinema_manager.repository.UserProfileRepository;
import com.re.cinema_manager.repository.UserRepository;
import com.re.cinema_manager.service.AdminUserService;
import com.re.cinema_manager.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserListItemDto> findAllUsers() {
        return userRepository.findAllWithProfile().stream()
                .map(u -> AdminUserListItemDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .role(u.getRole())
                        .fullName(u.getUserProfile() != null ? u.getUserProfile().getFullName() : "—")
                        .email(u.getUserProfile() != null ? u.getUserProfile().getEmail() : "—")
                        .phone(u.getUserProfile() != null ? u.getUserProfile().getPhone() : "")
                        .createdAt(u.getCreatedAt())
                        .bookingCount(bookingRepository.countByUserId(u.getId()))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserFormDto getFormForEdit(Long userId) {
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại."));
        AdminUserFormDto dto = new AdminUserFormDto();
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        if (user.getUserProfile() != null) {
            dto.setFullName(user.getUserProfile().getFullName());
            dto.setEmail(user.getUserProfile().getEmail());
            dto.setPhone(user.getUserProfile().getPhone());
        }
        return dto;
    }

    @Override
    @Transactional
    public void createUser(AdminUserFormDto form) {
        if (!StringUtils.hasText(form.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo tài khoản.");
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        User user = User.builder()
                .username(form.getUsername().trim())
                .password(PasswordUtil.createHash(form.getPassword()))
                .role(form.getRole())
                .build();
        User saved = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(saved)
                .fullName(form.getFullName().trim())
                .email(form.getEmail().trim())
                .phone(form.getPhone() != null ? form.getPhone().trim() : null)
                .build();
        try {
            userProfileRepository.save(profile);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
        }
    }

    @Override
    @Transactional
    public void updateUser(Long userId, AdminUserFormDto form) {
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại."));

        if (!user.getUsername().equals(form.getUsername().trim())
                && userRepository.existsByUsername(form.getUsername().trim())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        user.setUsername(form.getUsername().trim());
        user.setRole(form.getRole());
        if (StringUtils.hasText(form.getPassword())) {
            if (form.getPassword().length() < 6) {
                throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
            }
            user.setPassword(PasswordUtil.createHash(form.getPassword()));
        }

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
            user.setUserProfile(profile);
        }
        profile.setFullName(form.getFullName().trim());
        profile.setEmail(form.getEmail().trim());
        profile.setPhone(form.getPhone() != null ? form.getPhone().trim() : null);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại."));
        if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new IllegalArgumentException("Không thể xóa admin duy nhất còn lại.");
        }
        if (bookingRepository.countByUserId(userId) > 0) {
            throw new IllegalArgumentException(
                    "Tài khoản đã có đơn đặt vé — không xóa được. Có thể đổi vai trò hoặc vô hiệu hóa sau.");
        }
        userRepository.delete(user);
    }
}
