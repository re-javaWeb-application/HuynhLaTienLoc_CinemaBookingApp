-- ============================================================
-- RESET TOÀN BỘ DỮ LIỆU (chạy TAY trong MySQL khi cần làm sạch DB)
-- Không được đặt trong spring.sql.init — sẽ xóa cả tài khoản đăng ký.
-- Sau khi chạy file này, restart app để data.sql nạp lại mẫu (INSERT IGNORE).
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM tickets;
DELETE FROM payments;
DELETE FROM bookings;
DELETE FROM showtimes;
DELETE FROM seats;
DELETE FROM rooms;
DELETE FROM movies;
DELETE FROM user_profiles;
DELETE FROM users;
DELETE FROM genres;

SET FOREIGN_KEY_CHECKS = 1;
