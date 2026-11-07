package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nghiệp vụ quản trị sản phẩm/danh mục/thương hiệu/banner/người dùng; kiểm tra dữ liệu trước khi cập nhật repository.
 */
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.nova.dto.admin.AdminRequests;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.exception.ApiException;
import vn.nova.repository.AdminRepository;

/** Nghiệp vụ quản trị: lưu sản phẩm nguyên tử, giữ lịch sử SKU và kiểm tra các liên kết. */
@Service
public class AdminService {
    private final AdminRepository repository;
    private final TransactionTemplate transactions;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AdminService(AdminRepository repository, TransactionTemplate transactions) {
        this.repository = repository; this.transactions = transactions;
    }

    // Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL.
    public Map<String,Object> dashboard() { return repository.dashboard(); }
    // Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID.
    public Map<String,Object> product(long id) { return repository.product(id); }
    // Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại.
    public List<Map<String,Object>> categories() { return repository.categories(); }
    // Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị.
    public List<Map<String,Object>> brands() { return repository.brands(); }
    // Đọc các banner hợp lệ theo dữ liệu CSDL.
    public List<Map<String,Object>> banners() { return repository.banners(); }

    // Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại.
    public PageResponse<Map<String,Object>> products(String q, String status, long category, int page, int size) {
        pageRules(q,page,size);
        if (!List.of("","active","draft","archived").contains(status) || category < 0) throw ApiException.badRequest("Bộ lọc không hợp lệ.");
        return transactions.execute(tx -> {
            long total = repository.countProducts(q,status,category);
            return page(repository.products(q,status,category,page,size),total,page,size);
        });
    }

    // Kiểm tra dữ liệu sản phẩm, lưu các trường chính, SKU và ảnh bìa trong giao dịch.
    public Map<String,Long> saveProduct(Long id, AdminRequests.Product p) {
        image(p.imageUrl(), false);
        if (!repository.exists("brand",p.brandId()) || !repository.exists("category",p.categoryId())) throw ApiException.badRequest("Chọn danh mục và thương hiệu tồn tại.");
        var ids = new HashSet<Long>(); var skus = new HashSet<String>();
        for (var v : p.variants()) {
            if ((v.id()!=null && (!ids.add(v.id()) || id==null || v.id()<1)) || !skus.add(v.sku().trim())) throw ApiException.badRequest("SKU hoặc biến thể bị lặp/không hợp lệ.");
            if (v.originalPrice()!=null && v.originalPrice()<v.price()) throw ApiException.badRequest("Giá niêm yết không được thấp hơn giá bán.");
        }
        return transactions.execute(tx -> {
            if (id!=null) repository.lockProduct(id);
            long productId = id==null ? repository.createProduct(p) : id;
            if (id!=null) repository.updateProduct(id,p);
            // SKU bị bỏ khỏi form chỉ được ẩn, không xóa để giữ tham chiếu đơn hàng cũ.
            repository.deactivateVariants(productId);
            for (var variant : p.variants()) repository.saveVariant(productId,variant);
            repository.saveCover(productId, p.imageUrl()==null ? "" : p.imageUrl().trim(), p.name().trim());
            return Map.of("id",productId);
        });
    }

    // Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ.
    public void archiveProduct(long id) { transactions.executeWithoutResult(tx -> repository.archiveProduct(id)); }

    // Kiểm tra quan hệ cha/con rồi tạo hoặc cập nhật danh mục.
    public Map<String,Long> saveCategory(Long id, AdminRequests.Category c) {
        image(c.imageUrl(),false);
        return transactions.execute(tx -> {
            repository.lockCategoryTree();
            if (c.parentId()!=null && (!repository.exists("category",c.parentId()) || (id!=null && repository.categoryCycle(id,c.parentId())))) throw ApiException.badRequest("Danh mục cha không hợp lệ hoặc tạo vòng lặp.");
            return Map.of("id",repository.saveCategory(id,c));
        });
    }

    // Tạo hoặc cập nhật thương hiệu sau khi chuẩn hóa dữ liệu.
    public Map<String,Long> saveBrand(Long id, AdminRequests.Brand b) {
        image(b.logoUrl(),false);
        return transactions.execute(tx -> Map.of("id",repository.saveBrand(id,b)));
    }

    // Tạo hoặc cập nhật banner với URL/trạng thái/liên kết sản phẩm hợp lệ.
    public Map<String,Long> saveBanner(Long id, AdminRequests.Banner b) {
        image(b.imageUrl(),true);
        if (b.productId()!=null && !repository.exists("product",b.productId())) throw ApiException.badRequest("Sản phẩm liên kết không tồn tại.");
        return transactions.execute(tx -> Map.of("id",repository.saveBanner(id,b)));
    }

    // Lấy danh sách người dùng theo bộ lọc và phân trang quản trị.
    public PageResponse<Map<String,Object>> users(String q, int page, int size) {
        pageRules(q,page,size);
        return transactions.execute(tx -> page(repository.users(q,page,size),repository.countUsers(q),page,size));
    }

    // Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị.
    public void userStatus(long actor, long userId, AdminRequests.UserStatus status) {
        if (actor==userId && !"active".equals(status.status())) throw ApiException.badRequest("Bạn không thể tự khóa tài khoản đang quản trị.");
        transactions.executeWithoutResult(tx -> repository.userStatus(userId,status.status()));
    }

    // Tạo cấu trúc trang dữ liệu quản trị từ danh sách và số lượng tổng.
    private PageResponse<Map<String,Object>> page(List<Map<String,Object>> items, long total, int page, int size) {
        long pages = (total+size-1)/size;
        return new PageResponse<>(items,total,page,size,pages,page+1<pages);
    }

    // Kiểm tra page/size trong giới hạn trước khi truy vấn quản trị.
    private void pageRules(String q, int page, int size) {
        if (q.length()>200 || page<0 || page>10000 || size<1 || size>100) throw ApiException.badRequest("Phân trang hoặc từ khóa không hợp lệ.");
    }

    // Chuẩn hóa/kiểm tra URL ảnh do dữ liệu quản trị cung cấp.
    private void image(String value, boolean required) {
        if (value==null || value.isBlank()) {
            if (required) throw ApiException.badRequest("Cần đường dẫn ảnh HTTPS.");
            return;
        }
        try {
            URI uri = URI.create(value.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost()==null || uri.getUserInfo()!=null) throw new IllegalArgumentException();
        } catch (IllegalArgumentException error) { throw ApiException.badRequest("Ảnh phải dùng URL HTTPS hợp lệ."); }
    }
}
