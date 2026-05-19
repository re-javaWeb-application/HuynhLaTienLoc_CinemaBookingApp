package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    // Hàm tự động sinh câu lệnh kiểm tra xem email đã ai đăng ký chưa
    boolean existsByEmail(String email);

    // Tìm UserProfile theo user_id (dùng cho ProfileService)
    Optional<UserProfile> findByUserId(Long userId);
}
