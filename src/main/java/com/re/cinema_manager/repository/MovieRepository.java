package com.re.cinema_manager.repository;

import com.re.cinema_manager.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    // Bạn có thể thêm các hàm tìm kiếm phim theo tên ở đây sau này nếu cần
}
