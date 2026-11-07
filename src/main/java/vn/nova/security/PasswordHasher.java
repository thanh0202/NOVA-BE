package vn.nova.security;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Băm mật khẩu bằng PBKDF2-HMAC-SHA256 với salt riêng; xác minh hash đã lưu và giữ tương thích mật khẩu cũ.
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Component;
import vn.nova.exception.ApiException;

/** PBKDF2 có salt riêng cho mỗi mật khẩu; giữ tương thích mật khẩu đã lưu của NOVA. */
@Component
public class PasswordHasher {
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private final SecureRandom random = new SecureRandom();

    // Áp dụng quy tắc tạo mật khẩu mới; bước đăng nhập dùng matches để vẫn chấp nhận hash mật khẩu cũ hợp lệ.
    public void validate(String password) {
        if (!PasswordRules.isValid(password)) {
            throw ApiException.badRequest(PasswordRules.MESSAGE);
        }
    }

    // Tạo salt ngẫu nhiên và dẫn xuất PBKDF2 cho mật khẩu mới hợp lệ.
    public String hash(String password) {
        validate(password);
        byte[] salt = new byte[SALT_BYTES];
        // Salt ngẫu nhiên làm hai tài khoản cùng mật khẩu có hash khác nhau.
        random.nextBytes(salt);
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + derive(password, salt, ITERATIONS);
    }

    // Đọc tham số/salt từ hash đã lưu và so sánh giá trị dẫn xuất; dữ liệu hash lỗi sẽ không xác thực được.
    public boolean matches(String password, String encodedPassword) {
        if (password == null || password.length() > 128 || encodedPassword == null) {
            return false;
        }
        try {
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            // Hash hỏng trong CSDL không được làm phát sinh lượng tính toán vô hạn.
            if (iterations < 10_000 || iterations > 2_000_000 || salt.length != SALT_BYTES) {
                return false;
            }
            String candidate = derive(password, salt, iterations);
            // So sánh chuyên dụng để hạn chế khác biệt thời gian theo vị trí ký tự sai.
            return MessageDigest.isEqual(parts[3].getBytes(StandardCharsets.UTF_8),
                    candidate.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    // Dẫn xuất khóa PBKDF2-HMAC-SHA256 từ mật khẩu, salt và số vòng lặp.
    private String derive(String password, byte[] salt, int iterations) {
        PBEKeySpec specification = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
        try {
            byte[] derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(specification).getEncoded();
            return Base64.getEncoder().encodeToString(derived);
        } catch (java.security.GeneralSecurityException error) {
            throw new IllegalStateException("Không khởi tạo được bộ mã hóa mật khẩu.", error);
        } finally {
            specification.clearPassword();
        }
    }
}
