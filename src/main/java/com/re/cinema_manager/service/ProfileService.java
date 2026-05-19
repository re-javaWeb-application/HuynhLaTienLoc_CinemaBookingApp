package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.ProfileViewModel;
import com.re.cinema_manager.model.dto.UpdateProfileRequest;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.UserProfile;
import com.re.cinema_manager.repository.UserProfileRepository;
import com.re.cinema_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * Lấy ProfileViewModel từ userId trong session.
     * Fetch eager UserProfile từ DB thông qua username.
     */
    public ProfileViewModel getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với id: " + userId));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ người dùng"));

        return ProfileViewModel.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .role(user.getRole().name())
                .joinDate(user.getCreated_at())
                .build();
    }

    /**
     * Cập nhật fullName và phone cho user.
     * Chỉ được sửa 2 trường này, các trường khác read-only.
     */
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ người dùng"));

        profile.setFullName(request.getFullName().trim());

        String phone = request.getPhone();
        profile.setPhone((phone != null && !phone.isBlank()) ? phone.trim() : null);

        userProfileRepository.save(profile);
    }
}
