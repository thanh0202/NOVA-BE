package vn.nova.config;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đăng ký interceptor kiểm tra quyền quản trị cho các đường dẫn /api/admin/**.
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.nova.security.AdminAccessInterceptor;

@Configuration
public class AdminWebConfiguration implements WebMvcConfigurer {
    private final AdminAccessInterceptor adminAccess;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AdminWebConfiguration(AdminAccessInterceptor adminAccess) {
        this.adminAccess = adminAccess;
    }

    @Override
    // Gắn bộ kiểm tra quyền quản trị vào các đường dẫn được cấu hình.
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAccess).addPathPatterns("/api/admin", "/api/admin/**");
    }
}
