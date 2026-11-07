package vn.nova.security;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Giới hạn số yêu cầu theo khóa trong cửa sổ thời gian bằng bộ nhớ của tiến trình backend.
 */
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import vn.nova.exception.ApiException;

/**
 * Giới hạn theo cửa sổ cố định cho một instance backend.
 * Số lần nhập sai OTP và thời gian gửi lại còn được lưu bền vững trong PostgreSQL.
 */
@Component
public class RateLimiter {
    private record Window(long expiresAt, int used) {
    }

    private static final int MAX_TRACKED_KEYS = 10_000;
    private final Map<String, Window> windows = new HashMap<>();

    // Đếm yêu cầu theo key trong cửa sổ thời gian và trả lỗi 429 khi vượt ngưỡng.
    public synchronized void check(String key, int maximumRequests, long windowSeconds) {
        long now = System.currentTimeMillis();
        windows.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= now);
        Window current = windows.get(key);
        if (current == null) {
            if (windows.size() >= MAX_TRACKED_KEYS) {
                throw tooManyRequests();
            }
            current = new Window(now + windowSeconds * 1000, 0);
        }
        if (current.used() >= maximumRequests) {
            throw tooManyRequests();
        }
        windows.put(key, new Window(current.expiresAt(), current.used() + 1));
    }

    // Tạo lỗi 429 khi số yêu cầu vượt giới hạn.
    private ApiException tooManyRequests() {
        return new ApiException(429, "RATE_LIMIT", "Bạn thử quá nhiều lần. Vui lòng thử lại sau.");
    }
}
