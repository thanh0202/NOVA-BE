package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Xử lý hồ sơ, đổi mật khẩu/email, thu hồi phiên khi cần và kiểm tra URL ảnh đại diện.
 */
import java.net.URI;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.nova.dto.MessageResponse;
import vn.nova.dto.auth.ChallengeResponse;
import vn.nova.dto.auth.VerifyCodeRequest;
import vn.nova.dto.user.ChangeEmailRequest;
import vn.nova.dto.user.ChangePasswordRequest;
import vn.nova.dto.user.UpdateProfileRequest;
import vn.nova.dto.user.UserProfileResponse;
import vn.nova.exception.ApiException;
import vn.nova.model.VerificationPurpose;
import vn.nova.repository.UserRepository;
import vn.nova.security.PasswordHasher;
import vn.nova.security.RateLimiter;
import vn.nova.util.RequestRules;

/** Nghiệp vụ hồ sơ: userId luôn lấy từ token, không lấy userId do client tùy ý gửi. */
@Service
public class UserService {
    private final UserRepository users;
    private final SessionService sessions;
    private final VerificationService verification;
    private final PasswordHasher passwords;
    private final RateLimiter rateLimiter;
    private final TransactionTemplate transactions;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public UserService(UserRepository users, SessionService sessions, VerificationService verification,
                       PasswordHasher passwords, RateLimiter rateLimiter, TransactionTemplate transactions) {
        this.users = users;
        this.sessions = sessions;
        this.verification = verification;
        this.passwords = passwords;
        this.rateLimiter = rateLimiter;
        this.transactions = transactions;
    }

    // Xác thực Bearer token rồi đọc hồ sơ của chủ phiên.
    public UserProfileResponse getProfile(String authorization) {
        return getProfileById(sessions.requireUserId(authorization));
    }

    // Đọc DTO hồ sơ theo userId đã được nghiệp vụ xác định.
    public UserProfileResponse getProfileById(long userId) {
        return users.findProfile(userId);
    }

    // Kiểm tra dữ liệu hồ sơ và cập nhật các trường hợp lệ trong giao dịch.
    public UserProfileResponse updateProfile(String authorization, UpdateProfileRequest request) {
        long userId = sessions.requireUserId(authorization);
        String phone = RequestRules.normalizePhone(request.phone());
        String gender = request.gender() == null || request.gender().isBlank()
                ? "undisclosed" : request.gender();
        if (!Set.of("male", "female", "other", "undisclosed").contains(gender)) {
            throw ApiException.badRequest("Giới tính không hợp lệ.");
        }
        String avatar = request.avatarUrl() == null ? "" : request.avatarUrl().trim();
        if (request.birthDate() != null && request.birthDate().isBefore(java.time.LocalDate.of(1900, 1, 1))) {
            throw ApiException.badRequest("Ngày sinh không hợp lệ.");
        }
        validateAvatar(avatar);
        users.updateProfile(userId, request.name().trim(), RequestRules.nullIfBlank(phone),
                gender, request.avatarUrl()!=null, RequestRules.nullIfBlank(avatar), request.birthDate() == null ? null : request.birthDate().toString());
        return users.findProfile(userId);
    }

    // Kiểm tra mật khẩu hiện tại, lưu hash mới và thu hồi phiên để yêu cầu đăng nhập lại.
    public MessageResponse changePassword(String authorization, ChangePasswordRequest request) {
        long userId = sessions.requireUserId(authorization);
        String newHash = passwords.hash(request.newPassword());
        return transactions.execute(status -> {
            var account = users.findByIdForUpdate(userId);
            if (!passwords.matches(request.currentPassword(), account.passwordHash())) {
                throw ApiException.badRequest("Mật khẩu hiện tại chưa đúng.");
            }
            users.updatePassword(userId, newHash);
            verification.invalidateAll(userId);
            sessions.revokeAll(userId);
            return new MessageResponse("Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
        });
    }

    // Kiểm tra email/mật khẩu và phát challenge xác minh tới email mới.
    public ChallengeResponse changeEmail(String authorization, ChangeEmailRequest request) {
        long userId = sessions.requireUserId(authorization);
        rateLimiter.check("change-email:" + userId, 5, 600);
        String destination = RequestRules.normalizeEmail(request.email());
        return transactions.execute(status -> {
            var account = users.findByIdForUpdate(userId);
            if (!passwords.matches(request.password(), account.passwordHash())) {
                throw ApiException.badRequest("Mật khẩu hiện tại chưa đúng.");
            }
            if (users.emailExists(destination)) {
                throw new ApiException(409, "EMAIL_UNAVAILABLE", "Email không khả dụng.");
            }
            return verification.issueLocked(userId, destination, VerificationPurpose.EMAIL_CHANGE);
        });
    }

    // Tiêu thụ challenge đổi email, cập nhật địa chỉ mới và thu hồi phiên cũ.
    public MessageResponse verifyEmailChange(String authorization, VerifyCodeRequest request) {
        long userId = sessions.requireUserId(authorization);
        return verification.verify(request.challengeId(), request.code(), VerificationPurpose.EMAIL_CHANGE,
                challenge -> {
                    if (challenge.userId() != userId) {
                        throw ApiException.unauthorized();
                    }
                    users.updateEmail(userId, challenge.destination());
                    verification.invalidateAll(userId);
                    sessions.revokeAll(userId);
                    return new MessageResponse("Đã xác thực email mới. Vui lòng đăng nhập lại.");
                });
    }

    // Kiểm tra URL ảnh đại diện theo các điều kiện server chấp nhận.
    private void validateAvatar(String avatar) {
        if (avatar.isEmpty()) {
            return;
        }
        URI uri;
        try {
            uri = URI.create(avatar);
        } catch (IllegalArgumentException error) {
            throw ApiException.badRequest("Ảnh đại diện phải là URL HTTPS.");
        }
        if (!"https".equals(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null) {
            throw ApiException.badRequest("Ảnh đại diện phải là URL HTTPS.");
        }
    }
}
