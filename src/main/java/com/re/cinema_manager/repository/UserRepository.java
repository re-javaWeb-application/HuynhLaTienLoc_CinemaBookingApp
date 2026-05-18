package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Hàm này giúp Spring tự động tạo lệnh: SELECT * FROM users WHERE username = ?
    // Dùng để kiểm tra xem tài khoản đã tồn tại chưa khi đăng ký, và tìm user khi đăng nhập
    Optional<User> findUserByUsername(String username);

    // Tương tự, dùng để kiểm tra xem email đã có ai dùng chưa
    boolean existsUserByUsername(String username);
}
