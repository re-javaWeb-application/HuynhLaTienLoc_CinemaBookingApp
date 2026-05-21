package com.re.cinema_manager.util;

/**
 * Chuẩn hóa URL poster trước khi lưu DB / hiển thị &lt;img&gt;.
 */
public final class PosterUrlUtil {

    private PosterUrlUtil() {
    }

    /**
     * Trim, thêm https:// nếu thiếu scheme, trả null nếu rỗng.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith("//")) {
            return "https:" + trimmed;
        }
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    public static boolean looksLikeImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String lower = url.toLowerCase();
        return lower.contains(".jpg") || lower.contains(".jpeg") || lower.contains(".png")
                || lower.contains(".webp") || lower.contains(".gif")
                || lower.contains("image.tmdb.org") || lower.contains("/th/id/");
    }
}
