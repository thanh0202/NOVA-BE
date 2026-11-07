package vn.nova.model;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Record bộ lọc sản phẩm: chuẩn hóa tham số HTTP, alias danh mục/sắp xếp và kiểm tra giá, cờ, đánh giá, phân trang.
 */
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import vn.nova.exception.ApiException;

/** Bộ lọc đã kiểm tra hợp lệ trước khi Repository tạo truy vấn SQL. */
public record CatalogFilter(String keyword, String categoryCode, String brand,
                             Long minimumPriceVnd, Long maximumPriceVnd, BigDecimal minimumRating,
                             boolean onlyInStock, Integer storageGb, Integer ramGb,
                             boolean onlyFeatured, String sort, int page, int size) {
    private static final Set<String> ALLOWED_SORTS = Set.of(
            "featured", "bestselling", "newest", "price_asc", "price_desc", "rating");

    // Đọc query HTTP, chuẩn hóa alias, kiểm tra khoảng giá/phân trang và tạo record bộ lọc bất biến.
    public static CatalogFilter from(Map<String, String> values) {
        String keyword = text(values, "q", 150);
        String category = normalizeCategory(text(values, "category", 80));
        String brand = text(values, "brand", 100);
        Long minimum = integer(values, "minPrice", 0, 1_000_000_000_000L);
        Long maximum = integer(values, "maxPrice", 0, 1_000_000_000_000L);
        if (minimum != null && maximum != null && minimum > maximum) {
            throw ApiException.badRequest("Giá thấp nhất phải nhỏ hơn hoặc bằng giá cao nhất.");
        }
        BigDecimal rating = parseRating(text(values, "minRating", 12));
        Long storage = integer(values, "storageGb", 1, 100_000);
        Long ram = integer(values, "ramGb", 1, 10_000);
        String sort = text(values, "sort", 40);
        sort = switch (sort) {
            case "", "featured" -> "featured";
            case "price-asc" -> "price_asc";
            case "price-desc" -> "price_desc";
            default -> sort;
        };
        if (!ALLOWED_SORTS.contains(sort)) {
            throw ApiException.badRequest("Kiểu sắp xếp không được hỗ trợ.");
        }
        Long page = integer(values, "page", 0, 10_000);
        Long size = integer(values, "size", 1, 60);
        return new CatalogFilter(keyword, category, brand, minimum, maximum, rating,
                flag(values, "inStock"), storage == null ? null : storage.intValue(),
                ram == null ? null : ram.intValue(), flag(values, "featured"), sort,
                page == null ? 0 : page.intValue(), size == null ? 12 : size.intValue());
    }

    /** Cho phép app dùng slug tiếng Việt, còn CSDL giữ mã danh mục ngắn. */
    // Đổi slug/tên alias danh mục từ FE thành mã dùng trong truy vấn.
    public static String normalizeCategory(String value) {
        return switch (value) {
            case "dien-thoai" -> "phone";
            case "may-tinh", "laptop" -> "laptop";
            case "am-thanh" -> "audio";
            case "phu-kien" -> "accessory";
            default -> value;
        };
    }

    // Đọc số nguyên trong giới hạn và báo lỗi khi tham số sai định dạng.
    private static Long integer(Map<String, String> values, String key, long min, long max) {
        String raw = text(values, key, 24);
        if (raw.isEmpty()) {
            return null;
        }
        long value;
        try {
            value = Long.parseLong(raw);
        } catch (NumberFormatException error) {
            throw ApiException.badRequest(key + " phải là số nguyên hợp lệ.");
        }
        if (value < min || value > max) {
            throw ApiException.badRequest(key + " phải nằm trong khoảng " + min + " đến " + max + ".");
        }
        return value;
    }

    // Đọc cờ boolean và từ chối giá trị khác true/false.
    private static boolean flag(Map<String, String> values, String key) {
        String raw = text(values, key, 8);
        if (raw.isEmpty() || raw.equals("false")) {
            return false;
        }
        if (raw.equals("true")) {
            return true;
        }
        throw ApiException.badRequest(key + " chỉ nhận true hoặc false.");
    }

    // Đọc và cắt chuỗi query, áp dụng giới hạn độ dài của bộ lọc.
    private static String text(Map<String, String> values, String key, int maximumLength) {
        String value = values.getOrDefault(key, "").trim();
        if (value.length() > maximumLength) {
            throw ApiException.badRequest(key + " quá dài.");
        }
        return value;
    }

    // Đọc mức đánh giá tối thiểu và kiểm tra giới hạn hợp lệ.
    private static BigDecimal parseRating(String raw) {
        if (raw.isEmpty()) {
            return null;
        }
        BigDecimal rating;
        try {
            rating = new BigDecimal(raw);
        } catch (NumberFormatException error) {
            throw ApiException.badRequest("Điểm đánh giá không hợp lệ.");
        }
        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw ApiException.badRequest("Điểm đánh giá phải từ 0 đến 5.");
        }
        return rating;
    }
}
