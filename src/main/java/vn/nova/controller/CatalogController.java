package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các endpoint công khai cho danh mục, thương hiệu, tìm sản phẩm, chi tiết sản phẩm và banner.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.service.CatalogService;

/** Danh mục, thương hiệu, chi tiết sản phẩm và tìm kiếm nâng cao. */
@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService catalog;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping({"/products", "/products/search"})
    // Chuẩn hóa bộ lọc và trả trang sản phẩm phù hợp.
    public PageResponse<Map<String, Object>> search(@RequestParam Map<String, String> parameters) {
        return catalog.search(parameters, false);
    }

    @GetMapping("/products/{id}")
    // Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID.
    public Map<String, Object> product(@PathVariable long id) {
        return catalog.product(id);
    }

    @GetMapping("/banners")
    // Đọc các banner hợp lệ theo dữ liệu CSDL.
    public List<Map<String, Object>> banners() {
        return catalog.banners();
    }

    @GetMapping("/categories")
    // Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại.
    public List<Map<String, Object>> categories() {
        return catalog.categories();
    }

    @GetMapping("/brands")
    // Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị.
    public List<Map<String, Object>> brands(@RequestParam(defaultValue = "") String category) {
        return catalog.brands(category);
    }
}
