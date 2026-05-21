package com.re.cinema_manager.util;

/**
 * Mã vé chuẩn: BK-{id}. Hỗ trợ nhập "BK-12", "bk-12" hoặc "12".
 */
public final class BookingCodeUtil {

    public static final String PREFIX = "BK-";

    private BookingCodeUtil() {
    }

    public static String format(Long bookingId) {
        return PREFIX + bookingId;
    }

    public static Long parseId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.toUpperCase().startsWith(PREFIX)) {
            trimmed = trimmed.substring(PREFIX.length()).trim();
        }
        try {
            long id = Long.parseLong(trimmed);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
