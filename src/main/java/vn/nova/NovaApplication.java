package vn.nova;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Điểm vào Spring Boot; khởi tạo application context và quét component trong package vn.nova.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Điểm khởi động của backend NOVA. Spring tự tìm các bean bên trong package vn.nova. */
@SpringBootApplication
public class NovaApplication {
    // Khởi động SpringApplication và toàn bộ bean backend.
    public static void main(String[] args) {
        SpringApplication.run(NovaApplication.class, args);
    }
}
