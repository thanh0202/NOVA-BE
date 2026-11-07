package vn.nova.config;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Cấu hình CORS từ biến môi trường để kiểm soát nguồn web được phép gọi API.
 */
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Chỉ cho phép các origin web được khai báo trong cấu hình; Android native không dùng CORS. */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final String[] allowedOrigins;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public WebConfiguration(@Value("${nova.cors-origins}") String origins) {
        allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    // Áp dụng danh sách origin, method và header được phép qua CORS.
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .maxAge(3600);
    }
}
