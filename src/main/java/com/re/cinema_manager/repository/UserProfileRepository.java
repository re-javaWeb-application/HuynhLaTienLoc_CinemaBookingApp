package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Integer> {

    // Hàm tự động sinh câu lệnh kiểm tra xem email đã ai đăng ký chưa
    boolean existsByEmail (String email);
}
