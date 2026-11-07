package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Endpoint kiểm tra tình trạng API và khả năng truy vấn CSDL.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nova.service.HealthService;

/** Dùng khi tự triển khai để kiểm tra ứng dụng và kết nối CSDL. */
@RestController
public class HealthController {
    private final HealthService health;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public HealthController(HealthService health) {
        this.health = health;
    }

    @GetMapping("/api/health")
    // Nhận GET health và trả kết quả HealthService.
    public Map<String, String> check() {
        return health.check();
    }
}
