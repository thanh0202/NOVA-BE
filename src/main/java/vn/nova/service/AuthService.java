package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Điều phối đăng ký/đăng nhập; kiểm tra mật khẩu, trạng thái tài khoản, giới hạn tần suất và giao dịch tạo phiên/OTP.
 */
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.nova.dto.MessageResponse;
import vn.nova.dto.auth.AuthResponse;
import vn.nova.dto.auth.ChallengeResponse;
import vn.nova.dto.auth.EmailRequest;
import vn.nova.dto.auth.LoginRequest;
import vn.nova.dto.auth.RegisterRequest;
import vn.nova.dto.auth.ResetPasswordRequest;
import vn.nova.dto.auth.SessionResponse;
import vn.nova.dto.auth.VerifyCodeRequest;
import vn.nova.exception.ApiException;
import vn.nova.model.VerificationPurpose;
import vn.nova.repository.UserRepository;
import vn.nova.security.PasswordHasher;
import vn.nova.security.RateLimiter;
import vn.nova.security.TokenFactory;
import vn.nova.util.RequestRules;

/** Điều phối đăng ký/đăng nhập; logic OTP và phiên được tách thành hai service riêng. */
@Service
public class AuthService {
    private final UserRepository users;
    private final UserService profiles;
    private final SessionService sessions;
    private final VerificationService verification;
    private final PasswordHasher passwords;
    private final RateLimiter rateLimiter;
    private final TransactionTemplate transactions;
    private final String dummyPasswordHash;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AuthService(UserRepository users, UserService profiles, SessionService sessions,
                       VerificationService verification, PasswordHasher passwords,
                       RateLimiter rateLimiter, TransactionTemplate transactions, TokenFactory tokens) {
        this.users = users;
        this.profiles = profiles;
        this.sessions = sessions;
        this.verification = verification;
        this.passwords = passwords;
        this.rateLimiter = rateLimiter;
        this.transactions = transactions;
        // Vẫn tính PBKDF2 khi email không tồn tại để giảm khác biệt thời gian phản hồi.
        this.dummyPasswordHash = passwords.hash("Aa1!" + tokens.newToken());
    }

    // Chuẩn hóa dữ liệu đăng ký, tạo tài khoản/challenge OTP hoặc chuyển yêu cầu tới service tương ứng.
    public ChallengeResponse register(RegisterRequest request, String clientIp) {
        rateLimiter.check("register:" + clientIp, 10, 600);
        String email = RequestRules.normalizeEmail(request.email());
        String phone = RequestRules.normalizePhone(request.phone());
        String passwordHash = passwords.hash(request.password());
        return transactions.execute(status -> {
            if (users.emailExists(email)) {
                throw new ApiException(409, "EMAIL_UNAVAILABLE",
                        "Email không khả dụng. Hãy đăng nhập hoặc gửi lại mã xác thực.");
            }
            long userId = users.createAccount(email, passwordHash, request.name().trim(),
                    RequestRules.nullIfBlank(phone));
            users.createAccountDefaults(userId);
            return verification.issueLocked(userId, email, VerificationPurpose.EMAIL_VERIFY)
                    .withMessage("Đã gửi mã xác thực. Hãy xác thực email trước khi đăng nhập.");
        });
    }

    // Kiểm tra thông tin đăng nhập và cấp phản hồi phiên sau khi tài khoản đủ điều kiện.
    public AuthResponse login(LoginRequest request, String clientIp) {
        rateLimiter.check("login-ip:" + clientIp, 40, 600);
        String email = RequestRules.normalizeEmail(request.email());
        rateLimiter.check("login-email:" + email, 12, 600);
        return transactions.execute(status -> {
            var found = users.findByEmailForUpdate(email);
            String encodedPassword = found.map(account -> account.passwordHash()).orElse(dummyPasswordHash);
            boolean correct = passwords.matches(request.password(), encodedPassword);
            if (!correct || found.isEmpty() || !found.get().active()) {
                throw new ApiException(401, "BAD_CREDENTIALS", "Email hoặc mật khẩu chưa đúng.");
            }
            if (!found.get().emailVerified()) {
                throw new ApiException(403, "EMAIL_NOT_VERIFIED", "Hãy xác thực email trước khi đăng nhập.");
            }
            return createAuthResponse(found.get().id());
        });
    }

    // Giới hạn tần suất, tìm tài khoản đủ điều kiện và tạo phản hồi challenge không tiết lộ email có tồn tại hay không.
    public ChallengeResponse requestCode(EmailRequest request, VerificationPurpose purpose, String clientIp) {
        rateLimiter.check("otp-ip:" + clientIp, 20, 600);
        String email = RequestRules.normalizeEmail(request.email());
        rateLimiter.check("otp-email:" + purpose.databaseValue() + ":" + email, 5, 600);
        return transactions.execute(status -> {
            var account = users.findByEmailForUpdate(email);
            boolean eligible = account.isPresent() && account.get().active()
                    && (purpose != VerificationPurpose.EMAIL_VERIFY || !account.get().emailVerified());
            ChallengeResponse challenge = eligible
                    ? verification.issueLocked(account.get().id(), email, purpose)
                    : verification.genericChallenge();
            return challenge.withMessage("Nếu tài khoản phù hợp, mã xác thực đã được gửi đến email.");
        });
    }

    // Xác minh challenge OTP đăng ký rồi cập nhật trạng thái email và cấp phiên.
    public AuthResponse verifyEmail(VerifyCodeRequest request) {
        return verification.verify(request.challengeId(), request.code(), VerificationPurpose.EMAIL_VERIFY,
                challenge -> {
                    users.markEmailVerified(challenge.userId(), challenge.destination());
                    return createAuthResponse(challenge.userId());
                });
    }

    // Kiểm tra OTP khôi phục trước khi cho phép chuyển sang đặt mật khẩu mới.
    public MessageResponse verifyResetCode(VerifyCodeRequest request) {
        verification.checkRecoveryCode(request.challengeId(), request.code());
        return new MessageResponse("Mã hợp lệ. Hãy đặt mật khẩu mới.");
    }

    // Xác minh challenge, lưu hash mật khẩu mới và thu hồi các phiên cũ.
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        passwords.validate(request.newPassword());
        return verification.verify(request.challengeId(), request.code(), VerificationPurpose.PASSWORD_RESET,
                challenge -> {
                    users.updatePassword(challenge.userId(), passwords.hash(request.newPassword()));
                    verification.invalidateAll(challenge.userId());
                    sessions.revokeAll(challenge.userId());
                    return new MessageResponse("Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
                });
    }

    // Đọc hồ sơ của người dùng từ token hợp lệ để xác minh phiên hiện tại.
    public SessionResponse currentSession(String authorization) {
        return new SessionResponse(true, profiles.getProfile(authorization));
    }

    // Thu hồi phiên hiện tại và trả thông báo đăng xuất.
    public MessageResponse logout(String authorization) {
        return sessions.logout(authorization);
    }

    // Ghép token vừa tạo, thời hạn Bearer và hồ sơ thành AuthResponse.
    private AuthResponse createAuthResponse(long userId) {
        var session = sessions.createSession(userId);
        return new AuthResponse(session.token(), "Bearer", SessionService.SESSION_LIFETIME_SECONDS,
                session.expiresAt(), profiles.getProfileById(userId));
    }
}
