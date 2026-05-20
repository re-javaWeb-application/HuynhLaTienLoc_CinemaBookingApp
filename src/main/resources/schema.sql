-- ============================================================
-- CINEMA_MANAGER — Database schema (chuẩn dự án)
-- Spring Boot chạy file này khi khởi động (ddl-auto=none).
-- Kết nối JDBC đã trỏ sẵn database CINEMA_MANAGER — không dùng CREATE DATABASE / USE.
-- Thứ tự tạo bảng: bảng cha trước, bảng con (FK) sau.
-- ============================================================

-- 1. THỂ LOẠI PHIM
CREATE TABLE IF NOT EXISTS genres (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    genre_name  VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. NGƯỜI DÙNG
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'STAFF', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. HỒ SƠ NGƯỜI DÙNG (1-1 với users)
CREATE TABLE IF NOT EXISTS user_profiles (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   INT          NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    phone     VARCHAR(20),
    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. PHÒNG CHIẾU
CREATE TABLE IF NOT EXISTS rooms (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(50) NOT NULL UNIQUE,
    capacity  INT         NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. GHẾ (thuộc phòng)
CREATE TABLE IF NOT EXISTS seats (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    room_id   INT         NOT NULL,
    seat_name VARCHAR(10) NOT NULL,
    CONSTRAINT fk_seat_room
        FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT uk_seat_room_name UNIQUE (room_id, seat_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. PHIM
CREATE TABLE IF NOT EXISTS movies (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INT          NOT NULL,
    genre_id         INT,
    release_date     DATE,
    poster_url       VARCHAR(255),
    CONSTRAINT fk_movie_genre
        FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE SET NULL,
    CONSTRAINT chk_movie_duration CHECK (duration_minutes > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. SUẤT CHIẾU (CORE-05 — Showtime Scheduling)
CREATE TABLE IF NOT EXISTS showtimes (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    movie_id   INT      NOT NULL,
    room_id    INT      NOT NULL,
    start_time DATETIME NOT NULL,
    CONSTRAINT fk_showtime_movie
        FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE RESTRICT,
    CONSTRAINT fk_showtime_room
        FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    INDEX idx_showtime_room_start (room_id, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
