package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lưu digest token phiên, tìm người dùng của phiên còn hạn và thu hồi một hoặc mọi phiên.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import org.springframework.stereotype.Repository;
import vn.nova.util.JdbcExecutor;

/** Lưu phiên đăng nhập và thu hồi token; không lưu token gốc. */
@Repository
public class SessionRepository {
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public SessionRepository(JdbcExecutor database) {
        this.database = database;
    }

    // Lưu userId, digest của token và thời điểm hết hạn vào bảng phiên.
    public String create(long userId, String tokenDigest, int lifetimeSeconds) {
        return (String) database.findOne("""
                INSERT INTO shop.auth_sessions(user_id, refresh_token_hash, expires_at)
                VALUES (?, ?, now() + (? * interval '1 second'))
                RETURNING expires_at AS "expiresAt"
                """, userId, tokenDigest, lifetimeSeconds).get("expiresAt");
    }

    // Tìm userId từ digest phiên còn hạn và tài khoản đang hoạt động.
    public long findAuthenticatedUserId(String tokenDigest) {
        return database.findLong("""
                SELECT u.id FROM shop.auth_sessions session
                JOIN shop.users u ON u.id = session.user_id
                WHERE session.refresh_token_hash = ?
                  AND session.revoked_at IS NULL AND session.expires_at > now()
                  AND u.status = 'active' AND u.email_verified_at IS NOT NULL
                """, tokenDigest);
    }

    // Đánh dấu thu hồi một phiên token.
    public void revoke(String tokenDigest) {
        database.update("""
                UPDATE shop.auth_sessions SET revoked_at = now()
                WHERE refresh_token_hash = ? AND revoked_at IS NULL
                """, tokenDigest);
    }

    // Thu hồi mọi phiên còn hiệu lực của tài khoản.
    public void revokeAll(long userId) {
        database.update("""
                UPDATE shop.auth_sessions SET revoked_at = now()
                WHERE user_id = ? AND revoked_at IS NULL
                """, userId);
    }
}
