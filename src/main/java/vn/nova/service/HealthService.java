package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Tổng hợp tình trạng ứng dụng và kết quả kiểm tra kết nối cơ sở dữ liệu.
 */
import java.util.Map;
import org.springframework.stereotype.Service;
import vn.nova.repository.HealthRepository;

@Service
public class HealthService {
    private final HealthRepository health;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public HealthService(HealthRepository health) {
        this.health = health;
    }

    // Gọi repository kiểm tra CSDL rồi ghép phản hồi sức khỏe của ứng dụng.
    public Map<String, String> check() {
        health.checkDatabase();
        return Map.of("status", "UP", "database", "UP", "service", "nova");
    }
}
