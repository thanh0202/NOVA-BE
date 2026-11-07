package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Phát và kiểm tra challenge OTP theo mục đích; quản lý hạn dùng, số lần thử, tiêu thụ mã và giao dịch xác minh.
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.nova.dto.auth.ChallengeResponse;
import vn.nova.exception.ApiException;
import vn.nova.model.AccountRecord;
import vn.nova.model.VerificationChallenge;
import vn.nova.model.VerificationPurpose;
import vn.nova.repository.UserRepository;
import vn.nova.repository.VerificationRepository;
import vn.nova.security.TokenFactory;

/** OTP một lần: có thời hạn, cooldown gửi lại và số lần thử được lưu trong PostgreSQL. */
@Service
public class VerificationService {
    private static final int OTP_LIFETIME_SECONDS = 5 * 60;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final VerificationRepository challenges;
    private final UserRepository users;
    private final TokenFactory tokens;
    private final EmailService email;
    private final TransactionTemplate transactions;
    private final String secret;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public VerificationService(VerificationRepository challenges, UserRepository users,
                               TokenFactory tokens, EmailService email, TransactionTemplate transactions,
                               @Value("${nova.otp-key}") String secret) {
        if (secret.length() < 32) {
            throw new IllegalStateException("NOVA_OTP_KEY cần ít nhất 32 ký tự; chạy scripts/create-env.py để tạo.");
        }
        this.challenges = challenges;
        this.users = users;
        this.tokens = tokens;
        this.email = email;
        this.transactions = transactions;
        this.secret = secret;
    }

    /** Caller phải đang giữ khóa dòng user trong cùng transaction trước khi gọi hàm này. */
    // Tạo OTP cho tài khoản đã khóa trong giao dịch, lưu digest/hạn dùng và gửi email.
    public ChallengeResponse issueLocked(long userId, String destination, VerificationPurpose purpose) {
        if (challenges.recentlyIssued(userId, purpose, RESEND_COOLDOWN_SECONDS)) {
            throw new ApiException(429, "RESEND_TOO_SOON", "Vui lòng đợi 60 giây trước khi gửi lại mã.");
        }
        challenges.invalidatePurpose(userId, purpose);
        String challengeId = tokens.newToken();
        String code = tokens.newOtp();
        String expiresAt = challenges.create(userId, destination, purpose, challengeId,
                tokens.signOtp(challengeId, code, secret), OTP_LIFETIME_SECONDS);
        email.sendOtp(destination, code, purpose);
        return new ChallengeResponse(challengeId, expiresAt, OTP_LIFETIME_SECONDS,
                RESEND_COOLDOWN_SECONDS, null);
    }

    /** Phản hồi giả có cùng hình dạng để tránh tiết lộ email có tồn tại hay không. */
    // Trả challenge giả có cùng kiểu phản hồi để tránh tiết lộ trạng thái tài khoản.
    public ChallengeResponse genericChallenge() {
        return new ChallengeResponse(tokens.newToken(), null, OTP_LIFETIME_SECONDS,
                RESEND_COOLDOWN_SECONDS, null);
    }

    // Vô hiệu hóa các challenge còn sử dụng được của tài khoản.
    public void invalidateAll(long userId) {
        challenges.invalidateAll(userId);
    }

    // Kiểm tra và tiêu thụ OTP theo mục đích rồi thực hiện nghiệp vụ nằm trong callback.
    public <T> T verify(String challengeId, String code, VerificationPurpose purpose,
                        Function<VerificationChallenge, T> onSuccess) {
        return check(challengeId, code, purpose, true, onSuccess);
    }

    /** Validate the recovery step without spending the code required by the final password update. */
    // Kiểm tra mã khôi phục còn hợp lệ mà chưa đổi mật khẩu ngay.
    public void checkRecoveryCode(String challengeId, String code) {
        check(challengeId, code, VerificationPurpose.PASSWORD_RESET, false, challenge -> Boolean.TRUE);
    }

    // Khóa challenge, kiểm tra mục đích/hạn dùng/số lần thử và so sánh digest OTP trước khi tiêu thụ.
    private <T> T check(String challengeId, String code, VerificationPurpose purpose, boolean consume,
                        Function<VerificationChallenge, T> onSuccess) {
        if (challengeId == null || challengeId.isBlank() || challengeId.length() > 64
                || code == null || !code.matches("[0-9]{6}")) {
            throw ApiException.badRequest("Mã xác thực phải gồm 6 chữ số và có challengeId hợp lệ.");
        }
        VerificationResult<T> result = transactions.execute(status -> {
            var owner = challenges.findOwner(challengeId, purpose);
            if (owner.isEmpty()) {
                return failed(invalidCode());
            }
            // Giữ cùng thứ tự khóa với resend/đổi email: user trước, challenge sau.
            AccountRecord account = users.findByIdForUpdate(owner.get());
            VerificationChallenge challenge = challenges.findForUpdate(challengeId, purpose);
            if (!account.active() || !challenge.live() || challenge.consumed()
                    || challenge.attempts() >= MAX_FAILED_ATTEMPTS) {
                return failed(invalidCode());
            }
            if (purpose != VerificationPurpose.EMAIL_CHANGE
                    && !account.email().equals(challenge.destination())) {
                return failed(invalidCode());
            }
            String candidate = tokens.signOtp(challengeId, code, secret);
            boolean matches = MessageDigest.isEqual(
                    challenge.tokenHash().getBytes(StandardCharsets.US_ASCII),
                    candidate.getBytes(StandardCharsets.US_ASCII));
            if (!matches) {
                challenges.incrementAttempts(challenge.id());
                return failed(invalidCode());
            }
            if (consume) challenges.consume(challenge.id());
            return new VerificationResult<>(onSuccess.apply(challenge), null);
        });
        // Lỗi OTP được ném SAU khi transaction commit để không rollback bộ đếm số lần sai.
        // Nếu callback thất bại, TransactionTemplate vẫn rollback việc consume mã.
        Objects.requireNonNull(result);
        if (result.error() != null) {
            throw result.error();
        }
        return result.value();
    }

    private record VerificationResult<T>(T value, ApiException error) {
    }

    // Ghi nhận số lần nhập sai và tạo lỗi xác minh phù hợp.
    private static <T> VerificationResult<T> failed(ApiException error) {
        return new VerificationResult<>(null, error);
    }

    // Tạo lỗi chung khi mã sai, hết hạn hoặc challenge không còn dùng được.
    private ApiException invalidCode() {
        return new ApiException(400, "OTP_INVALID", "Mã xác thực không hợp lệ, đã dùng hoặc đã hết hạn.");
    }
}
