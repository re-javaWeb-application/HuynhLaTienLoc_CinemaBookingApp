package com.re.cinema_manager.util;

import com.re.cinema_manager.model.entity.BookingStatus;

import java.time.LocalDateTime;

/**
 * CORE-09: Quy tắc hủy vé.
 * <ul>
 *   <li>Đã thanh toán (PAID — online hoặc sau khi quầy xác nhận): hủy trước ≥ 24h so với giờ chiếu.</li>
 *   <li>Chờ thanh toán tại quầy (PENDING): hủy được đến trước giờ chiếu (chưa thanh toán).</li>
 * </ul>
 */
public final class BookingPolicy {

    public static final int CANCEL_MIN_HOURS_BEFORE_SHOWTIME = 24;

    private BookingPolicy() {
    }

    public record CancelDecision(boolean allowed, String blockReason) {
        public static CancelDecision ok() {
            return new CancelDecision(true, null);
        }
    }

    public static CancelDecision evaluateCancel(BookingStatus status, LocalDateTime showtimeStart) {
        LocalDateTime now = LocalDateTime.now();

        if (status == BookingStatus.CANCELLED) {
            return new CancelDecision(false, "Đơn đặt vé đã được hủy trước đó.");
        }
        if (status != BookingStatus.PAID && status != BookingStatus.PENDING) {
            return new CancelDecision(false, "Không thể hủy đơn ở trạng thái hiện tại.");
        }
        if (!showtimeStart.isAfter(now)) {
            return new CancelDecision(false, "Suất chiếu đã qua — không thể hủy vé.");
        }

        if (status == BookingStatus.PENDING) {
            return CancelDecision.ok();
        }

        if (showtimeStart.isAfter(now.plusHours(CANCEL_MIN_HOURS_BEFORE_SHOWTIME))) {
            return CancelDecision.ok();
        }

        return new CancelDecision(false,
                "Vé đã thanh toán chỉ hủy được trước ít nhất "
                        + CANCEL_MIN_HOURS_BEFORE_SHOWTIME
                        + " giờ so với giờ chiếu. Vui lòng liên hệ quầy vé.");
    }

    public static boolean canCancelByTime(LocalDateTime showtimeStart) {
        return showtimeStart.isAfter(LocalDateTime.now().plusHours(CANCEL_MIN_HOURS_BEFORE_SHOWTIME));
    }

    public static boolean canCancelBooking(BookingStatus status, LocalDateTime showtimeStart) {
        return evaluateCancel(status, showtimeStart).allowed();
    }

    public static String cancelDeniedMessage(CancelDecision decision) {
        if (decision.blockReason() != null) {
            return decision.blockReason();
        }
        return cancelTooLateMessage();
    }

    public static String cancelTooLateMessage() {
        return "Vé đã thanh toán chỉ hủy được trước ít nhất "
                + CANCEL_MIN_HOURS_BEFORE_SHOWTIME
                + " giờ so với giờ chiếu. Vui lòng liên hệ quầy vé.";
    }

    /** Nhãn hiển thị khi không hiện nút hủy (PAID/PENDING còn hiệu lực). */
    public static String cancelBlockHint(BookingStatus status, LocalDateTime showtimeStart) {
        CancelDecision d = evaluateCancel(status, showtimeStart);
        if (d.allowed()) {
            return null;
        }
        return d.blockReason();
    }
}
