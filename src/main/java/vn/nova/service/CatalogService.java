package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chuẩn hóa bộ lọc, gọi CatalogRepository và ghép dữ liệu chi tiết gồm biến thể, ảnh, thông số.
 */
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.exception.ApiException;
import vn.nova.model.CatalogFilter;
import vn.nova.repository.CatalogRepository;
import vn.nova.repository.ProductSearchSql;

/** Nghiệp vụ đọc catalog; Controller không cần biết cấu trúc SQL bên dưới. */
@Service
public class CatalogService {
    private final CatalogRepository catalog;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public CatalogService(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    // Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại.
    public List<Map<String, Object>> categories() {
        List<Map<String, Object>> categories = catalog.findCategories();
        for (Map<String, Object> category : categories) {
            String code = category.get("code").toString();
            category.put("slug", switch (code) {
                case "phone" -> "dien-thoai";
                case "laptop" -> "may-tinh";
                case "audio" -> "am-thanh";
                case "accessory" -> "phu-kien";
                default -> code;
            });
        }
        return categories;
    }

    // Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị.
    public List<Map<String, Object>> brands(String category) {
        if (category.length() > 80) {
            throw ApiException.badRequest("category quá dài.");
        }
        return catalog.findBrands(CatalogFilter.normalizeCategory(category));
    }

    /** COUNT và danh sách dùng cùng snapshot, tránh tổng số bị lệch khi dữ liệu đang thay đổi. */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    // Chuẩn hóa bộ lọc và trả trang sản phẩm phù hợp.
    public PageResponse<Map<String, Object>> search(Map<String, String> parameters, boolean soldOnly) {
        CatalogFilter filter = CatalogFilter.from(parameters);
        ProductSearchSql query = ProductSearchSql.from(filter, soldOnly);
        long total = catalog.countProducts(query);
        var items = catalog.findProducts(query, filter.page(), filter.size());
        return PageResponse.of(items, total, filter.page(), filter.size());
    }

    // Đọc sản phẩm đang hoạt động rồi ghép biến thể, ảnh và thông số thành JSON chi tiết.
    public Map<String, Object> product(long productId) {
        Map<String, Object> product = catalog.findProduct(productId);
        product.put("variants", catalog.findVariants(productId));
        product.put("images", catalog.findImages(productId));
        product.put("specifications", catalog.findSpecifications(productId));
        return product;
    }

    // Đọc các banner hợp lệ theo dữ liệu CSDL.
    public List<Map<String, Object>> banners() {
        return catalog.findBanners();
    }
}
