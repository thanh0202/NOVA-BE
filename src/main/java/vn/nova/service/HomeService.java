package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Ghép dữ liệu Trang chủ và tạo các trang sản phẩm theo tiêu chí nổi bật, bán chạy hoặc mới về.
 */
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import vn.nova.util.JdbcExecutor;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.exception.ApiException;

/** Ghép dữ liệu cho trang Home và tái sử dụng cùng bộ lọc catalog cho các section. */
@Service
public class HomeService {
    private final CatalogService catalog;

    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public HomeService(CatalogService catalog, JdbcExecutor database) {
        this.catalog = catalog;
        this.database = database;
    }

    // Lấy sản phẩm mới về theo dữ liệu phiếu nhập được truy vấn.
    public List<Map<String, Object>> newArrivals() {
        return database.findMany("""
                SELECT p.id,p.name,b.name AS brand,arrival.last_received_at AS "lastReceivedAt",
                       price.amount AS price,
                       (SELECT im.url FROM shop.product_images im WHERE im.product_id=p.id
                        ORDER BY im.sort_order,im.id LIMIT 1) AS "imageUrl"
                FROM shop.v_product_new_arrivals arrival
                JOIN shop.products p ON p.id=arrival.product_id
                JOIN shop.brands b ON b.id=p.brand_id
                CROSS JOIN LATERAL (SELECT min(v.price_vnd) AS amount FROM shop.product_variants v
                                    WHERE v.product_id=p.id AND v.status='active') price
                ORDER BY arrival.last_received_at DESC,p.id DESC LIMIT 12
                """);
    }

    // Ghép các nhóm sản phẩm của Trang chủ trong một phản hồi.
    public Map<String, Object> home(int limit) {
        if (limit < 1 || limit > 20) {
            throw ApiException.badRequest("limit phải từ 1 đến 20.");
        }
        return Map.of(
                "serverTime", Instant.now().toString(),
                "categories", catalog.categories(),
                "banners", catalog.banners(),
                "newArrivals", newArrivals(),
                "featured", featured(Map.of("sort", "featured", "size", String.valueOf(limit))),
                "bestsellers", bestsellers(Map.of("size", String.valueOf(limit))),
                "sections", List.of(
                        Map.of("key", "featured", "title", "Sản phẩm nổi bật", "endpoint", "/api/home/featured"),
                        Map.of("key", "bestsellers", "title", "Sản phẩm bán chạy", "endpoint", "/api/home/bestsellers")));
    }

    // Lấy trang sản phẩm có tiêu chí nổi bật.
    public PageResponse<Map<String, Object>> featured(Map<String, String> parameters) {
        Map<String, String> filters = new HashMap<>(parameters);
        filters.put("featured", "true");
        return catalog.search(filters, false);
    }

    // Lấy trang sản phẩm bán chạy theo dữ liệu đã ghi nhận.
    public PageResponse<Map<String, Object>> bestsellers(Map<String, String> parameters) {
        Map<String, String> filters = new HashMap<>(parameters);
        filters.put("sort", "bestselling");
        return catalog.search(filters, true);
    }
}
