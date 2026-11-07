package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Truy vấn và cập nhật dữ liệu quản trị; khóa sản phẩm/cây danh mục, lưu ảnh, SKU, banner và trạng thái tài khoản.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import vn.nova.dto.admin.AdminRequests;
import vn.nova.util.JdbcExecutor;

/** SQL quản trị. Các điều kiện lọc dùng tham số và chỉ có các lựa chọn ORDER BY cố định. */
@Repository
public class AdminRepository {
    private final JdbcExecutor db;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AdminRepository(JdbcExecutor db) { this.db = db; }

    // Kiểm tra người dùng có vai trò quản trị đang hoạt động.
    public boolean isAdmin(long userId) {
        return db.findLong("""
                SELECT count(*) FROM shop.user_roles ur JOIN shop.roles r ON r.id = ur.role_id
                WHERE ur.user_id = ? AND r.code = 'ADMIN'
                """, userId) > 0;
    }

    // Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL.
    public Map<String, Object> dashboard() {
        return db.findOne("""
                SELECT (SELECT count(*) FROM shop.products) AS products,
                       (SELECT count(*) FROM shop.products WHERE status = 'active') AS "activeProducts",
                       (SELECT count(*) FROM shop.products WHERE is_featured AND status = 'active') AS featured,
                       (SELECT count(*) FROM shop.users) AS users,
                       (SELECT count(*) FROM shop.categories WHERE is_active) AS categories,
                       (SELECT count(*) FROM shop.home_banners WHERE is_active) AS banners,
                       (SELECT count(*) FROM shop.orders) AS orders,
                       (SELECT coalesce(sum(total_vnd), 0) FROM shop.orders
                        WHERE status = 'delivered' AND payment_status IN ('paid','partially_refunded','refunded')) AS revenue
                """);
    }

    // Dựng phần điều kiện truy vấn quản trị với các nhánh SQL do server xác định.
    private String productWhere() {
        return """
                WHERE (? = '' OR p.name ILIKE ? ESCAPE '!' OR p.code ILIKE ? ESCAPE '!')
                  AND (? = '' OR p.status = ?) AND (? = 0 OR p.category_id = ?)
                """;
    }

    // Tạo danh sách tham số theo đúng thứ tự dấu ? trong SQL sản phẩm.
    private Object[] productParameters(String q, String status, long category) {
        String escaped = "%" + q.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        return new Object[]{q, escaped, escaped, status, status, category, category};
    }

    // Đếm tổng sản phẩm theo cùng điều kiện dùng để lấy danh sách.
    public long countProducts(String q, String status, long category) {
        return db.findLong("SELECT count(*) FROM shop.products p " + productWhere(), productParameters(q, status, category));
    }

    // Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại.
    public List<Map<String, Object>> products(String q, String status, long category, int page, int size) {
        List<Object> args = new ArrayList<>(List.of(productParameters(q, status, category)));
        args.add(size); args.add((long) page * size);
        return db.findMany("""
                SELECT p.id, p.code, p.name, p.slug, p.status, p.is_featured AS featured,
                       p.category_id AS "categoryId", c.name AS category, b.name AS brand,
                       (SELECT min(v.price_vnd) FROM shop.product_variants v
                        WHERE v.product_id = p.id AND v.status = 'active') AS price,
                       (SELECT count(*) FROM shop.product_variants v WHERE v.product_id = p.id) AS "variantCount",
                       (SELECT i.url FROM shop.product_images i WHERE i.product_id = p.id ORDER BY i.sort_order,i.id LIMIT 1) AS "imageUrl",
                       p.updated_at AS "updatedAt"
                FROM shop.products p JOIN shop.categories c ON c.id = p.category_id JOIN shop.brands b ON b.id = p.brand_id
                """ + productWhere() + " ORDER BY p.updated_at DESC, p.id DESC LIMIT ? OFFSET ?", args.toArray());
    }

    // Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID.
    public Map<String, Object> product(long id) {
        Map<String, Object> p = db.findOne("""
                SELECT id, code, name, slug, summary, description, brand_id AS "brandId", category_id AS "categoryId",
                       status, is_featured AS featured,
                       (SELECT url FROM shop.product_images WHERE product_id=p.id ORDER BY sort_order,id LIMIT 1) AS "imageUrl"
                FROM shop.products p WHERE id=?
                """, id);
        p.put("variants", db.findMany("""
                SELECT id, sku, color, storage_gb AS "storageGb", ram_gb AS "ramGb", price_vnd AS price,
                       list_price_vnd AS "originalPrice", status
                FROM shop.product_variants WHERE product_id=? ORDER BY id
                """, id));
        var images = images(id);
        p.put("images",images);
        p.put("imageCount",images.size());
        p.put("maxImages",imageLimit(id));
        return p;
    }

    // Đọc các URL và thứ tự ảnh hiện có của sản phẩm để kiểm tra giới hạn/chống trùng.
    public List<Map<String,Object>> images(long id) {
        return db.findMany("SELECT id,url,alt_text AS alt,sort_order AS \"sortOrder\" FROM shop.product_images WHERE product_id=? ORDER BY sort_order,id",id);
    }

    /** Sản phẩm trong danh mục con cũng nhận giới hạn của nhóm thiết bị cha. */
    // Dựa vào cây danh mục để giới hạn mười ảnh cho nhóm thiết bị chính và năm ảnh cho các nhóm còn lại.
    public int imageLimit(long id) {
        long mainDevice = db.findLong("""
                WITH RECURSIVE ancestors AS (
                  SELECT c.id,c.parent_id,c.code FROM shop.categories c JOIN shop.products p ON p.category_id=c.id WHERE p.id=?
                  UNION SELECT c.id,c.parent_id,c.code FROM shop.categories c JOIN ancestors a ON a.parent_id=c.id
                ) SELECT count(*) FROM ancestors WHERE code IN ('phone','laptop','audio','tablet','watch')
                """,id);
        return mainDevice>0 ? 10 : 5;
    }

    // Thêm URL ảnh mới vào bộ ảnh với thứ tự tiếp theo của sản phẩm.
    public void appendImage(long id,String url,String name) {
        db.update("""
                INSERT INTO shop.product_images(product_id,url,alt_text,sort_order)
                SELECT ?,?,?,coalesce(max(sort_order),-1)+1 FROM shop.product_images WHERE product_id=?
                """,id,url,name,id);
        db.update("UPDATE shop.products SET updated_at=now() WHERE id=?",id);
    }

    // Khóa hàng sản phẩm trong giao dịch trước khi tính giới hạn ảnh/cập nhật dữ liệu.
    public void lockProduct(long id) { db.findOne("SELECT id FROM shop.products WHERE id=? FOR UPDATE", id); }
    // Kiểm tra một bản ghi tham chiếu có tồn tại trước khi ghi dữ liệu liên quan.
    public boolean exists(String kind, long id) {
        // Tên bảng chỉ do server lựa chọn, không nhận trực tiếp từ request.
        String table = switch (kind) { case "brand" -> "brands"; case "category" -> "categories"; default -> "products"; };
        return db.findLong("SELECT count(*) FROM shop." + table + " WHERE id=?", id) > 0;
    }

    // Kiểm tra/tạo sản phẩm và các dữ liệu liên quan theo tầng controller/repository.
    public long createProduct(AdminRequests.Product p) {
        return db.findLong("""
                INSERT INTO shop.products(code,name,slug,summary,description,brand_id,category_id,status,is_featured)
                VALUES(?,?,?,?,?,?,?,?,?) RETURNING id
                """, p.code().trim(), p.name().trim(), p.slug(), p.summary(), p.description(), p.brandId(), p.categoryId(), p.status(), p.featured());
    }

    // Cập nhật các trường sản phẩm đã được kiểm tra hợp lệ.
    public void updateProduct(long id, AdminRequests.Product p) {
        db.update("""
                UPDATE shop.products SET code=?,name=?,slug=?,summary=?,description=?,brand_id=?,category_id=?,
                       status=?,is_featured=?,updated_at=now() WHERE id=?
                """, p.code().trim(), p.name().trim(), p.slug(), p.summary(), p.description(), p.brandId(), p.categoryId(), p.status(), p.featured(), id);
    }

    // Ngừng các biến thể cũ trước khi áp dụng bộ SKU cập nhật.
    public void deactivateVariants(long productId) {
        db.update("UPDATE shop.product_variants SET status='inactive',updated_at=now() WHERE product_id=?", productId);
    }

    // Tạo mới hoặc cập nhật một SKU của sản phẩm.
    public void saveVariant(long productId, AdminRequests.Variant v) {
        if (v.id() == null) {
            db.update("""
                    INSERT INTO shop.product_variants(product_id,sku,color,storage_gb,ram_gb,price_vnd,list_price_vnd,status,price_kind)
                    VALUES(?,?,?,?,?,?,?,?,'demo')
                    """, productId, v.sku().trim(), v.color().trim(), v.storageGb(), v.ramGb(), v.price(), v.originalPrice(), v.status());
        } else {
            db.findOne("SELECT id FROM shop.product_variants WHERE id=? AND product_id=? FOR UPDATE", v.id(), productId);
            db.update("""
                    UPDATE shop.product_variants SET sku=?,color=?,storage_gb=?,ram_gb=?,
                           price_kind=CASE WHEN price_vnd<>? THEN 'demo' ELSE price_kind END,
                           price_vnd=?,list_price_vnd=?,status=?,updated_at=now() WHERE id=? AND product_id=?
                    """, v.sku().trim(), v.color().trim(), v.storageGb(), v.ramGb(), v.price(), v.price(), v.originalPrice(), v.status(), v.id(), productId);
        }
    }

    // Lưu URL ảnh bìa được chọn của sản phẩm.
    public void saveCover(long productId, String imageUrl, String name) {
        var image = db.findOptional("SELECT id FROM shop.product_images WHERE product_id=? ORDER BY sort_order,id LIMIT 1", productId);
        if (image.isPresent()) {
            db.update("UPDATE shop.product_images SET url=?,alt_text=? WHERE id=?", imageUrl, name, image.get().get("id"));
        } else if (!imageUrl.isBlank()) {
            db.update("INSERT INTO shop.product_images(product_id,url,alt_text,sort_order) VALUES(?,?,?,0)", productId, imageUrl, name);
        }
    }

    // Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ.
    public void archiveProduct(long id) { lockProduct(id); db.update("UPDATE shop.products SET status='archived',updated_at=now() WHERE id=?", id); }

    // Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại.
    public List<Map<String, Object>> categories() {
        return db.findMany("""
                SELECT c.id,c.code,c.name,c.parent_id AS "parentId",c.sort_order AS "sortOrder",c.is_active AS active,c.image_url AS "imageUrl",
                       (SELECT count(*) FROM shop.products p WHERE p.category_id=c.id) AS "productCount"
                FROM shop.categories c ORDER BY c.sort_order,c.id
                """);
    }

    // Lấy khóa khi cập nhật cây danh mục để kiểm tra quan hệ nhất quán.
    public void lockCategoryTree() {
        // Tuần tự hóa thay đổi cây danh mục, tránh hai request đồng thời tạo vòng lặp.
        db.findOne("SELECT pg_advisory_xact_lock(7827914) AS locked");
    }

    // Kiểm tra thay đổi cha có tạo vòng lặp trong cây danh mục hay không.
    public boolean categoryCycle(long id, long parentId) {
        return db.findLong("""
                WITH RECURSIVE ancestors AS (
                  SELECT id,parent_id FROM shop.categories WHERE id=?
                  UNION SELECT c.id,c.parent_id FROM shop.categories c JOIN ancestors a ON a.parent_id=c.id
                ) SELECT count(*) FROM ancestors WHERE id=?
                """, parentId, id) > 0;
    }

    // Kiểm tra quan hệ cha/con rồi tạo hoặc cập nhật danh mục.
    public long saveCategory(Long id, AdminRequests.Category c) {
        if (id == null) return db.findLong("""
                INSERT INTO shop.categories(code,name,parent_id,sort_order,is_active,image_url) VALUES(?,?,?,?,?,?) RETURNING id
                """, c.code(), c.name().trim(), c.parentId(), c.sortOrder(), c.active(), c.imageUrl());
        db.findOne("SELECT id FROM shop.categories WHERE id=? FOR UPDATE", id);
        db.update("UPDATE shop.categories SET code=?,name=?,parent_id=?,sort_order=?,is_active=?,image_url=? WHERE id=?",
                c.code(), c.name().trim(), c.parentId(), c.sortOrder(), c.active(), c.imageUrl(), id);
        return id;
    }

    // Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị.
    public List<Map<String, Object>> brands() {
        return db.findMany("""
                SELECT b.id,b.code,b.name,b.is_active AS active,b.logo_url AS "logoUrl",
                       (SELECT count(*) FROM shop.products p WHERE p.brand_id=b.id) AS "productCount"
                FROM shop.brands b ORDER BY b.name,b.id
                """);
    }

    // Tạo hoặc cập nhật thương hiệu sau khi chuẩn hóa dữ liệu.
    public long saveBrand(Long id, AdminRequests.Brand b) {
        if (id == null) return db.findLong("INSERT INTO shop.brands(code,name,is_active,logo_url) VALUES(?,?,?,?) RETURNING id", b.code(), b.name().trim(), b.active(), b.logoUrl());
        db.findOne("SELECT id FROM shop.brands WHERE id=? FOR UPDATE", id);
        db.update("UPDATE shop.brands SET code=?,name=?,is_active=?,logo_url=? WHERE id=?", b.code(), b.name().trim(), b.active(), b.logoUrl(), id);
        return id;
    }

    // Đọc các banner hợp lệ theo dữ liệu CSDL.
    public List<Map<String, Object>> banners() {
        return db.findMany("SELECT id,title,image_url AS \"imageUrl\",product_id AS \"productId\",is_active AS active FROM shop.home_banners ORDER BY id DESC");
    }

    // Tạo hoặc cập nhật banner với URL/trạng thái/liên kết sản phẩm hợp lệ.
    public long saveBanner(Long id, AdminRequests.Banner b) {
        if (id == null) return db.findLong("INSERT INTO shop.home_banners(title,image_url,product_id,is_active) VALUES(?,?,?,?) RETURNING id", b.title().trim(), b.imageUrl(), b.productId(), b.active());
        db.findOne("SELECT id FROM shop.home_banners WHERE id=? FOR UPDATE", id);
        db.update("UPDATE shop.home_banners SET title=?,image_url=?,product_id=?,is_active=? WHERE id=?", b.title().trim(), b.imageUrl(), b.productId(), b.active(), id);
        return id;
    }

    // Đếm tổng tài khoản theo bộ lọc của trang quản trị.
    public long countUsers(String q) {
        return db.findLong("SELECT count(*) FROM shop.users WHERE ?='' OR full_name ILIKE ? ESCAPE '!' OR email ILIKE ? ESCAPE '!'", q, like(q), like(q));
    }

    // Lấy danh sách người dùng theo bộ lọc và phân trang quản trị.
    public List<Map<String, Object>> users(String q, int page, int size) {
        return db.findMany("""
                SELECT u.id,u.email,u.full_name AS name,u.phone,u.status,u.email_verified_at IS NOT NULL AS "emailVerified",
                       u.created_at AS "createdAt",EXISTS(SELECT 1 FROM shop.user_roles ur JOIN shop.roles r ON r.id=ur.role_id
                                                        WHERE ur.user_id=u.id AND r.code='ADMIN') AS admin
                FROM shop.users u WHERE ?='' OR u.full_name ILIKE ? ESCAPE '!' OR u.email ILIKE ? ESCAPE '!'
                ORDER BY u.id DESC LIMIT ? OFFSET ?
                """, q, like(q), like(q), size, (long) page * size);
    }

    // Escape ký tự đặc biệt của LIKE trước khi dùng làm giá trị tìm kiếm.
    private String like(String q) { return "%" + q.replace("!","!!").replace("%","!%").replace("_","!_") + "%"; }

    // Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị.
    public void userStatus(long id, String status) {
        db.findOne("SELECT id FROM shop.users WHERE id=? FOR UPDATE", id);
        db.update("UPDATE shop.users SET status=?,updated_at=now() WHERE id=?", status, id);
        if (!"active".equals(status)) db.update("UPDATE shop.auth_sessions SET revoked_at=now() WHERE user_id=? AND revoked_at IS NULL", id);
    }
}
