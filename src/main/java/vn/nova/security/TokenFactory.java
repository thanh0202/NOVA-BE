package vn.nova.security;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Sinh token ngẫu nhiên, OTP và digest/HMAC; CSDL giữ giá trị dẫn xuất thay cho token phiên thuần.
 */
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/** Tạo token 256 bit, mã OTP và các digest; không phụ thuộc dữ liệu HTTP. */
@Component
public class TokenFactory {
    private final SecureRandom random = new SecureRandom();

    // Sinh token ngẫu nhiên bằng SecureRandom rồi mã hóa Base64 URL-safe.
    public String newToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Sinh mã OTP bằng nguồn ngẫu nhiên an toàn.
    public String newOtp() {
        return String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
    }

    // Tính SHA-256 để lưu/tra token bằng giá trị băm.
    public String digest(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("Không khởi tạo được bộ băm token.", error);
        }
    }

    // Ký dữ liệu challenge và OTP bằng HMAC để mã không dùng lại cho challenge khác.
    public String signOtp(String challengeId, String otp, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signed = mac.doFinal((challengeId + ":" + otp).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signed);
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("Không khởi tạo được bộ ký OTP.", error);
        }
    }
}
