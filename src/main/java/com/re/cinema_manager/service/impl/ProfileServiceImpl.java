package com.re.cinema_manager.service.impl;

import com.re.cinema_manager.dto.profile.ProfileViewModel;
import com.re.cinema_manager.dto.profile.UpdateProfileRequest;
import com.re.cinema_manager.model.entity.User;
import com.re.cinema_manager.model.entity.UserProfile;
import com.re.cinema_manager.repository.UserProfileRepository;
import com.re.cinema_manager.repository.UserRepository;
import com.re.cinema_manager.service.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public ProfileViewModel getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        UserProfile profile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ"));

        return ProfileViewModel.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .role(user.getRole().name())
                .joinDate(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ"));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        userProfileRepository.save(profile);
    }
}
