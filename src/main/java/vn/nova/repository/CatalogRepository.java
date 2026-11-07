package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các truy vấn JDBC đọc danh mục, thương hiệu, sản phẩm, biến thể, ảnh, thông số và banner trong schema shop.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import vn.nova.util.JdbcExecutor;

/** SQL đọc danh mục/sản phẩm. Map giữ đúng tên cột JSON của API Android/web hiện có. */
@Repository
public class CatalogRepository {
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public CatalogRepository(JdbcExecutor database) {
        this.database = database;
    }

    // Truy vấn danh mục đang hoạt động, ảnh và số sản phẩm phù hợp.
    public List<Map<String, Object>> findCategories() {
        return database.findMany("""
                SELECT c.id, c.code, c.name, c.parent_id AS "parentId",
                       coalesce(nullif(btrim(c.image_url), ''), cover.url) AS "imageUrl",
                       (SELECT count(*) FROM shop.products p
                        JOIN shop.brands b ON b.id = p.brand_id AND b.is_active
                        WHERE p.category_id IN (SELECT category_id FROM shop.v_category_descendants WHERE ancestor_id=c.id) AND p.status = 'active'
                          AND EXISTS (SELECT 1 FROM shop.product_variants v
                                      WHERE v.product_id = p.id AND v.status = 'active')) AS "productCount"
                FROM shop.categories c
                LEFT JOIN LATERAL (
                    SELECT im.url FROM shop.products p
                    JOIN shop.product_images im ON im.product_id = p.id
                    JOIN shop.brands b ON b.id = p.brand_id AND b.is_active
                    WHERE p.category_id IN (SELECT category_id FROM shop.v_category_descendants WHERE ancestor_id=c.id) AND p.status = 'active'
                      AND nullif(btrim(im.url), '') IS NOT NULL
                      AND EXISTS (SELECT 1 FROM shop.product_variants v
                                  WHERE v.product_id = p.id AND v.status = 'active')
                    ORDER BY p.is_featured DESC, p.id, im.sort_order, im.id LIMIT 1
                ) cover ON true
                WHERE c.is_active ORDER BY c.sort_order, c.id
                """);
    }

    // Truy vấn thương hiệu đang hoạt động theo danh mục và số sản phẩm.
    public List<Map<String, Object>> findBrands(String categoryCode) {
        return database.findMany("""
                SELECT b.id, b.code, b.name, b.logo_url AS "logoUrl",
                       count(DISTINCT p.id) AS "productCount"
                FROM shop.brands b JOIN shop.products p ON p.brand_id = b.id
                JOIN shop.categories c ON c.id = p.category_id
                WHERE b.is_active AND c.is_active AND p.status = 'active'
                  AND (? = '' OR p.category_id IN (SELECT d.category_id FROM shop.v_category_descendants d JOIN shop.categories root ON root.id=d.ancestor_id WHERE root.code=?))
                  AND EXISTS (SELECT 1 FROM shop.product_variants v
                              WHERE v.product_id = p.id AND v.status = 'active')
                GROUP BY b.id ORDER BY b.name, b.id
                """, categoryCode, categoryCode);
    }

    // Đếm tổng sản phẩm theo cùng điều kiện dùng để lấy danh sách.
    public long countProducts(ProductSearchSql query) {
        return database.findLong(query.commonTables() + "SELECT count(*) FROM cards",
                query.parameters().toArray());
    }

    // Lấy trang sản phẩm với SQL/tham số đã chuẩn hóa và thứ tự đã cho phép.
    public List<Map<String, Object>> findProducts(ProductSearchSql query, int page, int size) {
        List<Object> parameters = new ArrayList<>(query.parameters());
        parameters.add(size);
        parameters.add((long) page * size);
        return database.findMany(query.commonTables() + "SELECT * FROM cards ORDER BY "
                + query.orderBy() + " LIMIT ? OFFSET ?", parameters.toArray());
    }

    // Đọc dữ liệu sản phẩm theo ID, trả kết quả tùy chọn khi không tồn tại.
    public Map<String, Object> findProduct(long productId) {
        return database.findOne("""
                SELECT p.id, p.code, p.name, p.slug, p.summary, p.description,
                       p.category_id AS "categoryId", c.name AS category, b.name AS brand,
                       p.is_featured AS featured
                FROM shop.products p JOIN shop.brands b ON b.id = p.brand_id
                JOIN shop.categories c ON c.id = p.category_id
                WHERE p.id = ? AND p.status = 'active' AND b.is_active AND c.is_active
                  AND EXISTS (SELECT 1 FROM shop.product_variants v
                              WHERE v.product_id = p.id AND v.status = 'active')
                """, productId);
    }

    // Lấy các SKU của sản phẩm với thông tin giá/tồn kho tương ứng.
    public List<Map<String, Object>> findVariants(long productId) {
        return database.findMany("""
                SELECT v.id, v.sku, v.color, v.storage_gb AS "storageGb", v.ram_gb AS "ramGb",
                       v.price_vnd AS price, coalesce(v.list_price_vnd, v.price_vnd) AS "originalPrice",
                       coalesce((SELECT sum(on_hand - reserved) FROM shop.inventory_balances i
                                 WHERE i.variant_id = v.id), 0) AS stock
                FROM shop.product_variants v WHERE v.product_id = ? AND v.status = 'active'
                ORDER BY v.price_vnd, v.id
                """, productId);
    }

    // Lấy bộ ảnh sản phẩm theo thứ tự sort_order và ID.
    public List<Map<String, Object>> findImages(long productId) {
        return database.findMany("""
                SELECT id, url, alt_text AS alt, variant_id AS "variantId"
                FROM shop.product_images WHERE product_id = ? ORDER BY sort_order, id
                """, productId);
    }

    // Lấy các thông số kỹ thuật gắn với sản phẩm.
    public List<Map<String, Object>> findSpecifications(long productId) {
        return database.findMany("""
                SELECT definition.code, definition.name, specification.value_text AS value
                FROM shop.product_specifications specification
                JOIN shop.specification_definitions definition ON definition.id = specification.specification_id
                WHERE specification.product_id = ? ORDER BY definition.sort_order, definition.id
                """, productId);
    }

    // Lấy banner đang hoạt động cùng sản phẩm được liên kết.
    public List<Map<String, Object>> findBanners() {
        return database.findMany("""
                SELECT banner.id, banner.title, banner.image_url AS "imageUrl",
                       banner.product_id AS "productId"
                FROM shop.home_banners banner
                WHERE banner.is_active AND (banner.product_id IS NULL OR EXISTS (
                    SELECT 1 FROM shop.products p
                    JOIN shop.categories c ON c.id = p.category_id AND c.is_active
                    JOIN shop.brands b ON b.id = p.brand_id AND b.is_active
                    WHERE p.id = banner.product_id AND p.status = 'active'
                      AND EXISTS (SELECT 1 FROM shop.product_variants v
                                  WHERE v.product_id = p.id AND v.status = 'active')
                )) ORDER BY banner.id DESC LIMIT 10
                """);
    }
}
