package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Dựng CTE, tham số và thứ tự SQL tìm sản phẩm; các tiêu chí giá/RAM/bộ nhớ/tồn kho cùng áp dụng trên tập biến thể hợp lệ.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import java.util.ArrayList;
import java.util.List;
import vn.nova.model.CatalogFilter;

/** Tách bộ dựng SQL khỏi Repository để dễ đọc và kiểm tra thứ tự tham số. */
public record ProductSearchSql(String commonTables, List<Object> parameters, String orderBy) {
    // Dựng các CTE stock/matching/prices/ratings/cards; ràng buộc tham số bằng dấu ? và chỉ chọn ORDER BY trong danh sách server cho phép.
    public static ProductSearchSql from(CatalogFilter filter, boolean soldOnly) {
        List<Object> parameters = new ArrayList<>();
        StringBuilder variants = new StringBuilder("v.status = 'active'");
        // Giá, RAM, bộ nhớ và tồn kho phải cùng khớp trên MỘT biến thể sản phẩm.
        // Giá trị do client nhập được đưa vào parameters, không nối trực tiếp vào câu SQL.
        if (filter.minimumPriceVnd() != null) {
            variants.append(" AND v.price_vnd >= ?");
            parameters.add(filter.minimumPriceVnd());
        }
        if (filter.maximumPriceVnd() != null) {
            variants.append(" AND v.price_vnd <= ?");
            parameters.add(filter.maximumPriceVnd());
        }
        if (filter.storageGb() != null) {
            variants.append(" AND v.storage_gb = ?");
            parameters.add(filter.storageGb());
        }
        if (filter.ramGb() != null) {
            variants.append(" AND v.ram_gb = ?");
            parameters.add(filter.ramGb());
        }
        if (filter.onlyInStock()) {
            variants.append(" AND coalesce(inv.stock, 0) > 0");
        }
        StringBuilder products = new StringBuilder("p.status = 'active' AND c.is_active AND b.is_active");
        if (!filter.keyword().isBlank()) {
            products.append(" AND (p.name ILIKE ? ESCAPE '!' OR b.name ILIKE ? ESCAPE '!' OR p.code ILIKE ? ESCAPE '!')");
            String pattern = "%" + filter.keyword().replace("!", "!!")
                    .replace("%", "!%").replace("_", "!_") + "%";
            parameters.add(pattern);
            parameters.add(pattern);
            parameters.add(pattern);
        }
        // View descendants mở rộng danh mục gốc sang các nhóm con trước khi lọc product.category_id.
        if (!filter.categoryCode().isBlank()) {
            products.append(" AND p.category_id IN (SELECT d.category_id FROM shop.v_category_descendants d JOIN shop.categories root ON root.id=d.ancestor_id WHERE root.code=?)");
            parameters.add(filter.categoryCode());
        }
        if (!filter.brand().isBlank()) {
            products.append(" AND (b.code = ? OR b.name = ?)");
            parameters.add(filter.brand());
            parameters.add(filter.brand());
        }
        if (filter.onlyFeatured()) {
            products.append(" AND p.is_featured");
        }
        if (filter.minimumRating() != null) {
            products.append(" AND coalesce(r.rating, 0) >= ?");
            parameters.add(filter.minimumRating());
        }
        if (soldOnly) {
            products.append(" AND coalesce(s.units_sold, 0) > 0");
        }
        // CTE matching giữ các SKU hợp lệ; cards lấy giá thấp nhất trong chính các SKU đó.
        String commonTables = """
                WITH stock AS (
                    SELECT variant_id, sum(on_hand - reserved) AS stock
                    FROM shop.inventory_balances GROUP BY variant_id
                ), matching AS (
                    SELECT v.*, coalesce(inv.stock, 0) AS stock
                    FROM shop.product_variants v
                    LEFT JOIN stock inv ON inv.variant_id = v.id WHERE %s
                ), prices AS (
                    SELECT product_id, min(price_vnd) AS price, sum(stock) AS stock,
                           (array_agg(coalesce(list_price_vnd, price_vnd) ORDER BY price_vnd, id))[1] AS original_price,
                           (array_agg(id ORDER BY price_vnd, id))[1] AS matched_variant_id
                    FROM matching GROUP BY product_id
                ), ratings AS (
                    SELECT product_id, round(avg(rating), 2) AS rating, count(*) AS review_count
                    FROM shop.reviews WHERE status = 'published' GROUP BY product_id
                ), cards AS (
                    SELECT p.id, p.code, p.name, p.slug, p.summary, p.category_id AS "categoryId",
                           c.code AS "categoryCode", b.code AS "brandCode", b.name AS brand,
                           p.is_featured AS featured, p.created_at AS "createdAt",
                           v.price, v.original_price AS "originalPrice", v.stock,
                           v.matched_variant_id AS "matchedVariantId", coalesce(r.rating, 0) AS rating,
                           coalesce(r.review_count, 0) AS "reviewCount", coalesce(s.units_sold, 0) AS "unitsSold",
                           (SELECT im.url FROM shop.product_images im WHERE im.product_id = p.id
                            ORDER BY im.sort_order, im.id LIMIT 1) AS "imageUrl"
                    FROM shop.products p JOIN shop.categories c ON c.id = p.category_id
                    JOIN shop.brands b ON b.id = p.brand_id JOIN prices v ON v.product_id = p.id
                    LEFT JOIN ratings r ON r.product_id = p.id
                    LEFT JOIN shop.v_product_bestsellers s ON s.product_id = p.id
                    WHERE %s
                )
                """.formatted(variants, products);
        // ORDER BY chỉ lấy từ các hằng do server viết; không nhận SQL trực tiếp từ client.
        String orderBy = switch (filter.sort()) {
            case "price_asc" -> "price ASC, id ASC";
            case "price_desc" -> "price DESC, id ASC";
            case "bestselling" -> "\"unitsSold\" DESC, id ASC";
            case "newest" -> "\"createdAt\" DESC, id DESC";
            case "rating" -> "rating DESC, \"reviewCount\" DESC, id ASC";
            default -> "featured DESC, id ASC";
        };
        return new ProductSearchSql(commonTables, List.copyOf(parameters), orderBy);
    }
}
