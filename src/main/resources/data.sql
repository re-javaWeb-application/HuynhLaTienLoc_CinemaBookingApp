-- ============================================================
-- Cinema Manager - Seed data (mẫu đầy đủ để test)
-- Chạy mỗi lần khởi động app NHƯNG không xóa dữ liệu cũ.
-- Tài khoản đăng ký mới được giữ sau khi restart server.
--
-- Đăng nhập mẫu:
--   admin    / admin123
--   staff    / admin123
--   customer / 123456
--
-- Reset toàn bộ về mẫu ban đầu: chạy data-reset.sql (MySQL Workbench / CLI).
-- ============================================================

-- ========== THỂ LOẠI ==========
INSERT IGNORE INTO genres (id, genre_name) VALUES
(1, 'Hành động'),
(2, 'Kinh dị'),
(3, 'Hài hước'),
(4, 'Tình cảm'),
(5, 'Hoạt hình');

-- ========== TÀI KHOẢN (password_hash: salt:sha256(salt+password)) ==========
INSERT IGNORE INTO users (id, username, password_hash, role, created_at) VALUES
(1, 'admin',    'a1b2c3d4e5f6789012345678abcdef01:48a2e301de904ca5bd67572a781f2bcfb3cc2e65c57825d26e470c9f64971115', 'ADMIN',    CURRENT_TIMESTAMP),
(2, 'customer', 'fedcba9876543210fedcba9876543210:6b35128224ef1a484d3ad275dce1acd8fbce47e7e64df289795b1b2f81b290d6', 'CUSTOMER', CURRENT_TIMESTAMP),
(3, 'staff',    'a1b2c3d4e5f6789012345678abcdef01:48a2e301de904ca5bd67572a781f2bcfb3cc2e65c57825d26e470c9f64971115', 'STAFF',    CURRENT_TIMESTAMP);

INSERT IGNORE INTO user_profiles (user_id, full_name, email, phone) VALUES
(1, 'Quản Trị Viên',   'admin@cinema.com',    '0901234567'),
(2, 'Nguyễn Văn Khách', 'customer@cinema.com', '0912345678'),
(3, 'Trần Nhân Viên',   'staff@cinema.com',    '0923456789');

-- ========== PHIM (12 phim, đủ 5 thể loại) ==========
INSERT IGNORE INTO movies (id, title, description, duration_minutes, release_date, poster_url, genre_id) VALUES
(1,  'Avengers: Hồi Kết',     'Nhóm siêu anh hùng đối đầu Thanos để cứu vũ trụ.', 181, '2019-04-26',
     'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 1),
(2,  'Nhà Bà Nữ',              'Câu chuyện hài hước và ấm áp về một gia đình Sài Gòn.', 117, '2023-10-20',
     'https://th.bing.com/th/id/OIP.kH4xW43pp5Cp-UMFdUe5qAHaEK?w=329&h=185&c=7&r=0&o=7&pid=1.7&rm=3', 3),
(3,  'Mai',                    'Hành trình tìm lại bản thân của người phụ nữ trung niên.', 131, '2024-05-17',
     'https://th.bing.com/th/id/OIP.q9I1OSQLVMMSAC_--U4-owHaKf?w=125&h=180&c=7&r=0&o=7&pid=1.7&rm=3', 4),
(4,  'Deadpool & Wolverine',   'Deadpool và Wolverine hợp sức trong cuộc phiêu lưu mới.', 128, '2024-07-26',
     'https://th.bing.com/th/id/OIP.YZSIdMJi5gR-bRCO2b_IggHaEK?w=329&h=185&c=7&r=0&o=7&pid=1.7&rm=3', 1),
(5,  'Mắt Biếc',               'Chuyện tình tuổi học trò và những lựa chọn của Tuấn.', 117, '2019-12-20',
     'https://thegioidienanh.vn/stores/news_dataimages/hath/072019/09/15/5450_Main.jpg', 4),
(6,  'Bố Già',                 'Hài hước xúc động về người cha Sài Gòn và con trai.', 128, '2021-03-12',
     'https://static2.vieon.vn/vieplay-image/thumbnail_v4/2025/10/28/gcdzb00q_bogiadienanh2021_1920x1080.jpg', 3),
(7,  'The Conjuring 4',        'Cặp vợ chồng Warren điều tra hiện tượng siêu nhiên mới.', 115, '2025-09-05',
     'https://static1.srcdn.com/wordpress/wp-content/uploads/2024/10/the-conjuring_-last-rites-2025.jpg', 2),
(8,  'Inside Out 2',           'Riley bước vào tuổi teen với cảm xúc mới trong đầu.', 96, '2024-06-14',
     'https://image.tmdb.org/t/p/original/zTLVWHGiGcFDzm2GR7vF6IQGEkp.jpg', 5),
(9,  'Dune: Part Two',         'Paul Atreides tiếp tục hành trình trên hành tinh Arrakis.', 166, '2024-03-01',
     'https://cdn.mos.cms.futurecdn.net/3CouDeYLJqfervtNXXY5qH.jpg', 1),
(10, 'Lật Mặt 7',              'Phần mới trong series hành động nổi tiếng Việt Nam.', 138, '2024-04-26',
     'https://photo-baomoi.bmcdn.me/w700_r1/2024_03_13_17_48553023/057ac6914bdda283fbcc.jpg', 1),
(11, 'La Haine',               'Ba chàng trai trong đêm bạo loạn ngoại ô Paris.', 98, '1995-05-31',
     'https://wallpaper.forfun.com/fetch/7e/7ec11dd9f6862e5d885c6e2118b4a222.jpeg', 2),
(12, 'Kung Fu Panda 4',        'Po trở lại với cuộc phiêu lưu võ thuật hài hước.', 94, '2024-03-08',
     'https://i.ytimg.com/vi/_inKs4eeHiI/maxresdefault.jpg', 5);

-- ========== PHÒNG CHIẾU (10 phòng) ==========
INSERT IGNORE INTO rooms (id, room_name, capacity) VALUES
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

-- ========== GHẾ MẪU (mọi phòng 1–10: 5 hàng x 6 cột = 30 ghế/phòng) ==========
INSERT IGNORE INTO seats (room_id, seat_name) VALUES
-- Phòng 1
(1, 'A1'), (1, 'A2'), (1, 'A3'), (1, 'A4'), (1, 'A5'), (1, 'A6'),
(1, 'B1'), (1, 'B2'), (1, 'B3'), (1, 'B4'), (1, 'B5'), (1, 'B6'),
(1, 'C1'), (1, 'C2'), (1, 'C3'), (1, 'C4'), (1, 'C5'), (1, 'C6'),
(1, 'D1'), (1, 'D2'), (1, 'D3'), (1, 'D4'), (1, 'D5'), (1, 'D6'),
(1, 'E1'), (1, 'E2'), (1, 'E3'), (1, 'E4'), (1, 'E5'), (1, 'E6'),
-- Phòng 2 (Inside Out 2 và các suất khác dùng phòng này)
(2, 'A1'), (2, 'A2'), (2, 'A3'), (2, 'A4'), (2, 'A5'), (2, 'A6'),
(2, 'B1'), (2, 'B2'), (2, 'B3'), (2, 'B4'), (2, 'B5'), (2, 'B6'),
(2, 'C1'), (2, 'C2'), (2, 'C3'), (2, 'C4'), (2, 'C5'), (2, 'C6'),
(2, 'D1'), (2, 'D2'), (2, 'D3'), (2, 'D4'), (2, 'D5'), (2, 'D6'),
(2, 'E1'), (2, 'E2'), (2, 'E3'), (2, 'E4'), (2, 'E5'), (2, 'E6'),
-- Phòng 3
(3, 'A1'), (3, 'A2'), (3, 'A3'), (3, 'A4'), (3, 'A5'), (3, 'A6'),
(3, 'B1'), (3, 'B2'), (3, 'B3'), (3, 'B4'), (3, 'B5'), (3, 'B6'),
(3, 'C1'), (3, 'C2'), (3, 'C3'), (3, 'C4'), (3, 'C5'), (3, 'C6'),
(3, 'D1'), (3, 'D2'), (3, 'D3'), (3, 'D4'), (3, 'D5'), (3, 'D6'),
(3, 'E1'), (3, 'E2'), (3, 'E3'), (3, 'E4'), (3, 'E5'), (3, 'E6'),
-- Phòng 4 (IMAX)
(4, 'A1'), (4, 'A2'), (4, 'A3'), (4, 'A4'), (4, 'A5'), (4, 'A6'),
(4, 'B1'), (4, 'B2'), (4, 'B3'), (4, 'B4'), (4, 'B5'), (4, 'B6'),
(4, 'C1'), (4, 'C2'), (4, 'C3'), (4, 'C4'), (4, 'C5'), (4, 'C6'),
(4, 'D1'), (4, 'D2'), (4, 'D3'), (4, 'D4'), (4, 'D5'), (4, 'D6'),
(4, 'E1'), (4, 'E2'), (4, 'E3'), (4, 'E4'), (4, 'E5'), (4, 'E6'),
-- Phòng 5
(5, 'A1'), (5, 'A2'), (5, 'A3'), (5, 'A4'), (5, 'A5'), (5, 'A6'),
(5, 'B1'), (5, 'B2'), (5, 'B3'), (5, 'B4'), (5, 'B5'), (5, 'B6'),
(5, 'C1'), (5, 'C2'), (5, 'C3'), (5, 'C4'), (5, 'C5'), (5, 'C6'),
(5, 'D1'), (5, 'D2'), (5, 'D3'), (5, 'D4'), (5, 'D5'), (5, 'D6'),
(5, 'E1'), (5, 'E2'), (5, 'E3'), (5, 'E4'), (5, 'E5'), (5, 'E6'),
-- Phòng 6
(6, 'A1'), (6, 'A2'), (6, 'A3'), (6, 'A4'), (6, 'A5'), (6, 'A6'),
(6, 'B1'), (6, 'B2'), (6, 'B3'), (6, 'B4'), (6, 'B5'), (6, 'B6'),
(6, 'C1'), (6, 'C2'), (6, 'C3'), (6, 'C4'), (6, 'C5'), (6, 'C6'),
(6, 'D1'), (6, 'D2'), (6, 'D3'), (6, 'D4'), (6, 'D5'), (6, 'D6'),
(6, 'E1'), (6, 'E2'), (6, 'E3'), (6, 'E4'), (6, 'E5'), (6, 'E6'),
-- Phòng 7
(7, 'A1'), (7, 'A2'), (7, 'A3'), (7, 'A4'), (7, 'A5'), (7, 'A6'),
(7, 'B1'), (7, 'B2'), (7, 'B3'), (7, 'B4'), (7, 'B5'), (7, 'B6'),
(7, 'C1'), (7, 'C2'), (7, 'C3'), (7, 'C4'), (7, 'C5'), (7, 'C6'),
(7, 'D1'), (7, 'D2'), (7, 'D3'), (7, 'D4'), (7, 'D5'), (7, 'D6'),
(7, 'E1'), (7, 'E2'), (7, 'E3'), (7, 'E4'), (7, 'E5'), (7, 'E6'),
-- Phòng 8
(8, 'A1'), (8, 'A2'), (8, 'A3'), (8, 'A4'), (8, 'A5'), (8, 'A6'),
(8, 'B1'), (8, 'B2'), (8, 'B3'), (8, 'B4'), (8, 'B5'), (8, 'B6'),
(8, 'C1'), (8, 'C2'), (8, 'C3'), (8, 'C4'), (8, 'C5'), (8, 'C6'),
(8, 'D1'), (8, 'D2'), (8, 'D3'), (8, 'D4'), (8, 'D5'), (8, 'D6'),
(8, 'E1'), (8, 'E2'), (8, 'E3'), (8, 'E4'), (8, 'E5'), (8, 'E6'),
-- Phòng 9
(9, 'A1'), (9, 'A2'), (9, 'A3'), (9, 'A4'), (9, 'A5'), (9, 'A6'),
(9, 'B1'), (9, 'B2'), (9, 'B3'), (9, 'B4'), (9, 'B5'), (9, 'B6'),
(9, 'C1'), (9, 'C2'), (9, 'C3'), (9, 'C4'), (9, 'C5'), (9, 'C6'),
(9, 'D1'), (9, 'D2'), (9, 'D3'), (9, 'D4'), (9, 'D5'), (9, 'D6'),
(9, 'E1'), (9, 'E2'), (9, 'E3'), (9, 'E4'), (9, 'E5'), (9, 'E6'),
-- Phòng 10
(10, 'A1'), (10, 'A2'), (10, 'A3'), (10, 'A4'), (10, 'A5'), (10, 'A6'),
(10, 'B1'), (10, 'B2'), (10, 'B3'), (10, 'B4'), (10, 'B5'), (10, 'B6'),
(10, 'C1'), (10, 'C2'), (10, 'C3'), (10, 'C4'), (10, 'C5'), (10, 'C6'),
(10, 'D1'), (10, 'D2'), (10, 'D3'), (10, 'D4'), (10, 'D5'), (10, 'D6'),
(10, 'E1'), (10, 'E2'), (10, 'E3'), (10, 'E4'), (10, 'E5'), (10, 'E6');

-- ========== SUẤT CHIẾU (30 suất — chỉ seed lần đầu khi chưa có suất nào) ==========
INSERT INTO showtimes (movie_id, room_id, start_time)
SELECT movie_id, room_id, start_time FROM (
SELECT 2 AS movie_id, 1 AS room_id, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 9 HOUR AS start_time UNION ALL
SELECT 6, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 13 HOUR UNION ALL
SELECT 12, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 17 HOUR UNION ALL
SELECT 8, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 10 HOUR UNION ALL
SELECT 5, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 14 HOUR UNION ALL
SELECT 1, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 18 HOUR UNION ALL
SELECT 4, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 9 HOUR UNION ALL
SELECT 9, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 14 HOUR UNION ALL
SELECT 10, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 10 HOUR UNION ALL
SELECT 3, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 15 HOUR UNION ALL
SELECT 7, 6, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 20 HOUR UNION ALL
SELECT 11, 6, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 11 HOUR UNION ALL
SELECT 2, 7, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 19 HOUR UNION ALL
SELECT 8, 8, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 9 HOUR UNION ALL
SELECT 12, 8, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 13 HOUR UNION ALL
SELECT 6, 9, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 10 HOUR UNION ALL
SELECT 5, 9, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 16 HOUR UNION ALL
SELECT 1, 4, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 20 HOUR UNION ALL
SELECT 4, 10, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 9 HOUR UNION ALL
SELECT 3, 10, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 14 HOUR UNION ALL
SELECT 9, 4, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR UNION ALL
SELECT 7, 2, DATE_ADD(CURDATE(), INTERVAL 6 DAY) + INTERVAL 21 HOUR UNION ALL
SELECT 10, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 8 HOUR UNION ALL
SELECT 11, 6, DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 13 HOUR UNION ALL
SELECT 8, 3, DATE_ADD(CURDATE(), INTERVAL 8 DAY) + INTERVAL 10 HOUR UNION ALL
SELECT 2, 1, DATE_ADD(CURDATE(), INTERVAL 8 DAY) + INTERVAL 15 HOUR UNION ALL
SELECT 6, 5, DATE_ADD(CURDATE(), INTERVAL 9 DAY) + INTERVAL 11 HOUR UNION ALL
SELECT 12, 8, DATE_ADD(CURDATE(), INTERVAL 9 DAY) + INTERVAL 18 HOUR UNION ALL
SELECT 5, 3, DATE_ADD(CURDATE(), INTERVAL 10 DAY) + INTERVAL 9 HOUR UNION ALL
SELECT 1, 4, DATE_ADD(CURDATE(), INTERVAL 10 DAY) + INTERVAL 14 HOUR
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM showtimes LIMIT 1);
