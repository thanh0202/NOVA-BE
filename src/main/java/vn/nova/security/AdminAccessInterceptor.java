package vn.nova.security;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Đọc Bearer token, xác thực phiên và quyền ADMIN trước khi cho endpoint quản trị chạy.
 */
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.nova.exception.ApiException;
import vn.nova.repository.AdminRepository;
import vn.nova.service.SessionService;

/** Kiểm tra quyền tại server cho mọi /api/admin/**, kể cả khi gọi trực tiếp bằng Postman. */
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {
    private final SessionService sessions;
    private final AdminRepository admin;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AdminAccessInterceptor(SessionService sessions, AdminRepository admin) {
        this.sessions = sessions;
        this.admin = admin;
    }

    @Override
    // Xác thực người gọi trước controller và gắn userId quản trị vào request.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) return true;
        long userId = sessions.requireUserId(request.getHeader("Authorization"));
        if (!admin.isAdmin(userId)) {
            throw new ApiException(403, "ADMIN_REQUIRED", "Tài khoản chưa được cấp quyền quản trị.");
        }
        request.setAttribute("nova.adminId", userId);
        return true;
    }
}
