package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các endpoint tạo sản phẩm, danh mục, thương hiệu và banner cho tài khoản ADMIN.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.nova.dto.MessageResponse;
import vn.nova.dto.admin.AdminRequests;
import vn.nova.dto.catalog.PageResponse;
import vn.nova.service.AdminService;

/** API chỉ dành cho ADMIN; AdminAccessInterceptor kiểm tra toàn bộ đường dẫn của controller. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService admin;
    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AdminController(AdminService admin) { this.admin=admin; }

    // Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL.
    @GetMapping("/dashboard") public Map<String,Object> dashboard() { return admin.dashboard(); }
    // Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại.
    @GetMapping("/products") public PageResponse<Map<String,Object>> products(@RequestParam(defaultValue="") String q,
            @RequestParam(defaultValue="") String status, @RequestParam(defaultValue="0") long category,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="12") int size) {
        return admin.products(q.trim(),status,category,page,size);
    }
    // Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID.
    @GetMapping("/products/{id}") public Map<String,Object> product(@PathVariable long id) { return admin.product(id); }
    @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED)
    // Kiểm tra/tạo sản phẩm và các dữ liệu liên quan theo tầng controller/repository.
    public Map<String,Long> createProduct(@Valid @RequestBody AdminRequests.Product body) { return admin.saveProduct(null,body); }
    // Cập nhật các trường sản phẩm đã được kiểm tra hợp lệ.
    @PutMapping("/products/{id}") public Map<String,Long> updateProduct(@PathVariable long id,@Valid @RequestBody AdminRequests.Product body) { return admin.saveProduct(id,body); }
    // Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ.
    @DeleteMapping("/products/{id}") public MessageResponse archiveProduct(@PathVariable long id) { admin.archiveProduct(id); return new MessageResponse("Đã lưu trữ sản phẩm."); }

    // Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại.
    @GetMapping("/categories") public List<Map<String,Object>> categories() { return admin.categories(); }
    @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED)
    // Tạo danh mục sau khi kiểm tra dữ liệu đầu vào/quyền quản trị.
    public Map<String,Long> createCategory(@Valid @RequestBody AdminRequests.Category body) { return admin.saveCategory(null,body); }
    // Cập nhật danh mục theo ID và kiểm tra quan hệ cây.
    @PutMapping("/categories/{id}") public Map<String,Long> updateCategory(@PathVariable long id,@Valid @RequestBody AdminRequests.Category body) { return admin.saveCategory(id,body); }
    // Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị.
    @GetMapping("/brands") public List<Map<String,Object>> brands() { return admin.brands(); }
    @PostMapping("/brands") @ResponseStatus(HttpStatus.CREATED)
    // Tạo thương hiệu với các trường hợp lệ.
    public Map<String,Long> createBrand(@Valid @RequestBody AdminRequests.Brand body) { return admin.saveBrand(null,body); }
    // Cập nhật thương hiệu hiện có theo ID.
    @PutMapping("/brands/{id}") public Map<String,Long> updateBrand(@PathVariable long id,@Valid @RequestBody AdminRequests.Brand body) { return admin.saveBrand(id,body); }
    // Đọc các banner hợp lệ theo dữ liệu CSDL.
    @GetMapping("/banners") public List<Map<String,Object>> banners() { return admin.banners(); }
    @PostMapping("/banners") @ResponseStatus(HttpStatus.CREATED)
    // Tạo banner quản trị cùng URL ảnh và liên kết sản phẩm.
    public Map<String,Long> createBanner(@Valid @RequestBody AdminRequests.Banner body) { return admin.saveBanner(null,body); }
    // Cập nhật nội dung/trạng thái banner theo ID.
    @PutMapping("/banners/{id}") public Map<String,Long> updateBanner(@PathVariable long id,@Valid @RequestBody AdminRequests.Banner body) { return admin.saveBanner(id,body); }
    // Lấy danh sách người dùng theo bộ lọc và phân trang quản trị.
    @GetMapping("/users") public PageResponse<Map<String,Object>> users(@RequestParam(defaultValue="") String q,
            @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="12") int size) { return admin.users(q.trim(),page,size); }
    // Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị.
    @PutMapping("/users/{id}/status") public MessageResponse userStatus(@RequestAttribute("nova.adminId") long actor,
            @PathVariable long id,@Valid @RequestBody AdminRequests.UserStatus body) {
        admin.userStatus(actor,id,body); return new MessageResponse("Đã cập nhật trạng thái tài khoản.");
    }
}
