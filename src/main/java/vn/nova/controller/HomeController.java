package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các endpoint dữ liệu Trang chủ, sản phẩm nổi bật, bán chạy và hàng mới về.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.service.HomeService;

/** API gộp trang Home và API riêng cho các section phân trang. */
@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService home;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public HomeController(HomeService home) {
        this.home = home;
    }

    @GetMapping
    // Ghép các nhóm sản phẩm của Trang chủ trong một phản hồi.
    public Map<String, Object> home(@RequestParam(defaultValue = "8") int limit) {
        return home.home(limit);
    }

    @GetMapping("/featured")
    // Lấy trang sản phẩm có tiêu chí nổi bật.
    public PageResponse<Map<String, Object>> featured(@RequestParam Map<String, String> parameters) {
        return home.featured(parameters);
    }

    @GetMapping("/bestsellers")
    // Lấy trang sản phẩm bán chạy theo dữ liệu đã ghi nhận.
    public PageResponse<Map<String, Object>> bestsellers(@RequestParam Map<String, String> parameters) {
        return home.bestsellers(parameters);
    }
    @GetMapping("/new-arrivals")
    // Lấy sản phẩm mới về theo dữ liệu phiếu nhập được truy vấn.
    public java.util.List<Map<String, Object>> newArrivals() {
        return home.newArrivals();
    }
}
