package com.re.cinema_manager.service;

import com.re.cinema_manager.model.dto.ProfileViewModel;
import com.re.cinema_manager.model.dto.UpdateProfileRequest;
import jakarta.transaction.Transactional;

public interface ProfileService {

    /**
     * Lấy ProfileViewModel từ userId trong session.
     */
    ProfileViewModel getProfileByUserId(Long userId);

    /**
     * Cập nhật fullName và phone cho user.
     */
    @Transactional
    void updateProfile(Long userId, UpdateProfileRequest request);
}
