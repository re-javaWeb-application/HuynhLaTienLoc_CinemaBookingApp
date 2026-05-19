package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    // Tạm thời để trống, JpaRepository đã hỗ trợ sẵn các hàm tìm kiếm cơ bản
}
