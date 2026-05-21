package com.re.cinema_manager.util;

import java.time.LocalDateTime;

/**
 * CORE-09: Quy tắc hủy vé — phải hủy trước ít nhất 24 giờ so với giờ chiếu.
 */
public final class BookingPolicy {

    public static final int CANCEL_MIN_HOURS_BEFORE_SHOWTIME = 24;

    private BookingPolicy() {
    }

    public static boolean canCancelByTime(LocalDateTime showtimeStart) {
        LocalDateTime deadline = LocalDateTime.now().plusHours(CANCEL_MIN_HOURS_BEFORE_SHOWTIME);
        return showtimeStart.isAfter(deadline);
    }

    public static String cancelTooLateMessage() {
        return "Không thể hủy vé vì còn dưới "
                + CANCEL_MIN_HOURS_BEFORE_SHOWTIME
                + " giờ trước giờ chiếu. Vui lòng liên hệ quầy vé.";
    }
}
