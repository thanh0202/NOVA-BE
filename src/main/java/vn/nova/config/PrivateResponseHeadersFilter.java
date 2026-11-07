package vn.nova.config;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gắn header chống lưu cache cho phản hồi nhạy cảm liên quan tài khoản, phiên và quản trị.
 */
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Không để trình duyệt/proxy lưu cache response chứa phiên đăng nhập hoặc hồ sơ cá nhân. */
@Component
public class PrivateResponseHeadersFilter extends OncePerRequestFilter {
    @Override
    // Kiểm tra URL phản hồi và thêm header cache phù hợp trước khi tiếp tục filter chain.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/me")
                || path.startsWith("/api/users/me") || path.startsWith("/api/admin")) {
            response.setHeader("Cache-Control", "no-store");
        }
        if (path.equals("/admin") || path.startsWith("/admin/")) {
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("Referrer-Policy", "same-origin");
            response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' https: data:; font-src 'self'; connect-src 'self'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'");
            if (path.endsWith("/") || path.endsWith(".html") || path.equals("/admin")) {
                response.setHeader("Cache-Control", "no-store");
            }
        }
        filterChain.doFilter(request, response);
    }
}
