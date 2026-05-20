-- ============================================================
-- Cinema Manager - Seed data (mẫu đầy đủ để test)
-- Đăng nhập:
--   admin    / admin123
--   staff    / admin123  (cùng hash demo với admin)
--   customer / 123456
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM showtimes;
DELETE FROM seats;
DELETE FROM rooms;
DELETE FROM movies;
DELETE FROM user_profiles;
DELETE FROM users;
DELETE FROM genres;

SET FOREIGN_KEY_CHECKS = 1;

-- ========== THỂ LOẠI ==========
INSERT INTO genres (id, genre_name) VALUES
(1, 'Hành động'),
(2, 'Kinh dị'),
(3, 'Hài hước'),
(4, 'Tình cảm'),
(5, 'Hoạt hình');

-- ========== TÀI KHOẢN (password_hash: salt:sha256(salt+password)) ==========
INSERT INTO users (id, username, password_hash, role, created_at) VALUES
(1, 'admin',    'a1b2c3d4e5f6789012345678abcdef01:48a2e301de904ca5bd67572a781f2bcfb3cc2e65c57825d26e470c9f64971115', 'ADMIN',    CURRENT_TIMESTAMP),
(2, 'customer', 'fedcba9876543210fedcba9876543210:6b35128224ef1a484d3ad275dce1acd8fbce47e7e64df289795b1b2f81b290d6', 'CUSTOMER', CURRENT_TIMESTAMP),
(3, 'staff',    'a1b2c3d4e5f6789012345678abcdef01:48a2e301de904ca5bd67572a781f2bcfb3cc2e65c57825d26e470c9f64971115', 'STAFF',    CURRENT_TIMESTAMP);

INSERT INTO user_profiles (user_id, full_name, email, phone) VALUES
(1, 'Quản Trị Viên',   'admin@cinema.com',    '0901234567'),
(2, 'Nguyễn Văn Khách', 'customer@cinema.com', '0912345678'),
(3, 'Trần Nhân Viên',   'staff@cinema.com',    '0923456789');

-- ========== PHIM (12 phim, đủ 5 thể loại) ==========
INSERT INTO movies (id, title, description, duration_minutes, release_date, poster_url, genre_id) VALUES
(1,  'Avengers: Hồi Kết',     'Nhóm siêu anh hùng đối đầu Thanos để cứu vũ trụ.', 181, '2019-04-26',
     'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 1),
(2,  'Nhà Bà Nữ',              'Câu chuyện hài hước và ấm áp về một gia đình Sài Gòn.', 117, '2023-10-20',
     'https://image.tmdb.org/t/p/w500/8xVBqK0a8w8qKqKqKqKqKqKqKqK.jpg', 3),
(3,  'Mai',                    'Hành trình tìm lại bản thân của người phụ nữ trung niên.', 131, '2024-05-17',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 4),
(4,  'Deadpool & Wolverine',   'Deadpool và Wolverine hợp sức trong cuộc phiêu lưu mới.', 128, '2024-07-26',
     'https://image.tmdb.org/t/p/w500/uxbd5qK0a8w8qKqKqKqKqKqKqKqK.jpg', 1),
(5,  'Mắt Biếc',               'Chuyện tình tuổi học trò và những lựa chọn của Tuấn.', 117, '2019-12-20',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 4),
(6,  'Bố Già',                 'Hài hước xúc động về người cha Sài Gòn và con trai.', 128, '2021-03-12',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 3),
(7,  'The Conjuring 4',        'Cặp vợ chồng Warren điều tra hiện tượng siêu nhiên mới.', 115, '2025-09-05',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 2),
(8,  'Inside Out 2',           'Riley bước vào tuổi teen với cảm xúc mới trong đầu.', 96, '2024-06-14',
     'https://image.tmdb.org/t/p/w500/uxJxJkK0a8w8qKqKqKqKqKqKqKqK.jpg', 5),
(9,  'Dune: Part Two',         'Paul Atreides tiếp tục hành trình trên hành tinh Arrakis.', 166, '2024-03-01',
     'https://image.tmdb.org/t/p/w500/1pdfLV8OeX9FZQhK0qKqKqKqKqKqK.jpg', 1),
(10, 'Lật Mặt 7',              'Phần mới trong series hành động nổi tiếng Việt Nam.', 138, '2024-04-26',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 1),
(11, 'La Haine',               'Ba chàng trai trong đêm bạo loạn ngoại ô Paris.', 98, '1995-05-31',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 2),
(12, 'Kung Fu Panda 4',        'Po trở lại với cuộc phiêu lưu võ thuật hài hước.', 94, '2024-03-08',
     'https://image.tmdb.org/t/p/w500/placeholder.jpg', 5);

-- ========== PHÒNG CHIẾU (10 phòng) ==========
INSERT INTO rooms (id, room_name, capacity) VALUES
(1,  'Phòng 1 - Standard',  80),
(2,  'Phòng 2 - Standard', 100),
(3,  'Phòng 3 - VIP',       50),
(4,  'Phòng 4 - IMAX',     120),
(5,  'Phòng 5 - Standard',  90),
(6,  'Phòng 6 - Standard',  85),
(7,  'Phòng 7 - Couple',    40),
(8,  'Phòng 8 - Premium',   70),
(9,  'Phòng 9 - Standard',  95),
(10, 'Phòng 10 - Deluxe',   60);

-- ========== GHẾ MẪU (phòng 1 & 4 — 5 hàng x 6 cột) ==========
INSERT INTO seats (room_id, seat_name) VALUES
(1, 'A1'), (1, 'A2'), (1, 'A3'), (1, 'A4'), (1, 'A5'), (1, 'A6'),
(1, 'B1'), (1, 'B2'), (1, 'B3'), (1, 'B4'), (1, 'B5'), (1, 'B6'),
(1, 'C1'), (1, 'C2'), (1, 'C3'), (1, 'C4'), (1, 'C5'), (1, 'C6'),
(4, 'A1'), (4, 'A2'), (4, 'A3'), (4, 'A4'), (4, 'A5'), (4, 'A6'),
(4, 'B1'), (4, 'B2'), (4, 'B3'), (4, 'B4'), (4, 'B5'), (4, 'B6');

-- ========== SUẤT CHIẾU (30 suất — ngày tính từ NOW(), luôn là "sắp chiếu" khi restart app) ==========
INSERT INTO showtimes (movie_id, room_id, start_time) VALUES
(2,  1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 9 HOUR),
(6,  1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 13 HOUR),
(12, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 17 HOUR),
(8,  2, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 10 HOUR),
(5,  3, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 14 HOUR),
(1,  4, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 18 HOUR),
(4,  4, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 9 HOUR),
(9,  4, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 14 HOUR),
(10, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 10 HOUR),
(3,  5, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 15 HOUR),
(7,  6, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 20 HOUR),
(11, 6, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 11 HOUR),
(2,  7, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 19 HOUR),
(8,  8, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 9 HOUR),
(12, 8, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 13 HOUR),
(6,  9, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 10 HOUR),
(5,  9, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 16 HOUR),
(1,  4, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 20 HOUR),
(4,  10, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 9 HOUR),
(3,  10, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 14 HOUR),
(9,  4, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR),
(7,  2, DATE_ADD(CURDATE(), INTERVAL 6 DAY) + INTERVAL 21 HOUR),
(10, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 8 HOUR),
(11, 6, DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 13 HOUR),
(8,  3, DATE_ADD(CURDATE(), INTERVAL 8 DAY) + INTERVAL 10 HOUR),
(2,  1, DATE_ADD(CURDATE(), INTERVAL 8 DAY) + INTERVAL 15 HOUR),
(6,  5, DATE_ADD(CURDATE(), INTERVAL 9 DAY) + INTERVAL 11 HOUR),
(12, 8, DATE_ADD(CURDATE(), INTERVAL 9 DAY) + INTERVAL 18 HOUR),
(5,  3, DATE_ADD(CURDATE(), INTERVAL 10 DAY) + INTERVAL 9 HOUR),
(1,  4, DATE_ADD(CURDATE(), INTERVAL 10 DAY) + INTERVAL 14 HOUR);

ALTER TABLE users AUTO_INCREMENT = 4;
ALTER TABLE genres AUTO_INCREMENT = 6;
ALTER TABLE movies AUTO_INCREMENT = 13;
ALTER TABLE rooms AUTO_INCREMENT = 11;
ALTER TABLE seats AUTO_INCREMENT = 31;
ALTER TABLE showtimes AUTO_INCREMENT = 32;
