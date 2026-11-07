package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Tạo và xác thực phiên Bearer, đổi token thành userId, kiểm tra hạn dùng và thu hồi phiên.
 */
import org.springframework.stereotype.Service;
import vn.nova.dto.MessageResponse;
import vn.nova.exception.ApiException;
import vn.nova.model.SessionToken;
import vn.nova.repository.SessionRepository;
import vn.nova.security.TokenFactory;

/** Phiên đăng nhập opaque: token ngẫu nhiên, hạn 24 giờ và thu hồi được ngay trong CSDL. */
@Service
public class SessionService {
    public static final int SESSION_LIFETIME_SECONDS = 24 * 60 * 60;
    private final SessionRepository sessions;
    private final TokenFactory tokens;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public SessionService(SessionRepository sessions, TokenFactory tokens) {
        this.sessions = sessions;
        this.tokens = tokens;
    }

    // Sinh token ngẫu nhiên, lưu digest kèm hạn dùng và trả token thuần đúng lúc cấp phiên.
    public SessionToken createSession(long userId) {
        String rawToken = tokens.newToken();
        String expiresAt = sessions.create(userId, tokens.digest(rawToken), SESSION_LIFETIME_SECONDS);
        return new SessionToken(rawToken, expiresAt);
    }

    // Tách Bearer token, kiểm tra phiên hợp lệ và trả ID người dùng hoặc lỗi 401.
    public long requireUserId(String authorization) {
        String rawToken = extractToken(authorization);
        long userId = sessions.findAuthenticatedUserId(tokens.digest(rawToken));
        if (userId == 0) {
            throw ApiException.unauthorized();
        }
        return userId;
    }

    // Thu hồi phiên hiện tại và trả thông báo đăng xuất.
    public MessageResponse logout(String authorization) {
        requireUserId(authorization);
        sessions.revoke(tokens.digest(extractToken(authorization)));
        return new MessageResponse("Đã đăng xuất.");
    }

    // Thu hồi mọi phiên còn hiệu lực của tài khoản.
    public void revokeAll(long userId) {
        sessions.revokeAll(userId);
    }

    // Tách chuỗi token từ header Authorization theo định dạng Bearer.
    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() > 200) {
            throw ApiException.unauthorized();
        }
        String token = authorization.substring("Bearer ".length());
        if (!token.matches("[A-Za-z0-9_-]{43}")) {
            throw ApiException.unauthorized();
        }
        return token;
    }
}
