package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lưu challenge OTP, khóa hàng khi xác minh, tăng số lần thử, tiêu thụ hoặc vô hiệu hóa challenge.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import java.util.Optional;
import org.springframework.stereotype.Repository;
import vn.nova.model.VerificationChallenge;
import vn.nova.model.VerificationPurpose;
import vn.nova.util.JdbcExecutor;

/** SQL của OTP: thời hạn, cooldown và số lần sai đều dựa trên dữ liệu PostgreSQL. */
@Repository
public class VerificationRepository {
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public VerificationRepository(JdbcExecutor database) {
        this.database = database;
    }

    // Kiểm tra challenge vừa được phát để áp dụng thời gian chờ gửi lại.
    public boolean recentlyIssued(long userId, VerificationPurpose purpose, int cooldownSeconds) {
        return database.findLong("""
                SELECT count(*) FROM shop.verification_challenges
                WHERE user_id = ? AND purpose = ?
                  AND created_at > now() - (? * interval '1 second')
                """, userId, purpose.databaseValue(), cooldownSeconds) > 0;
    }

    // Vô hiệu hóa challenge còn dùng được của một mục đích xác minh.
    public void invalidatePurpose(long userId, VerificationPurpose purpose) {
        database.update("""
                UPDATE shop.verification_challenges SET consumed_at = now()
                WHERE user_id = ? AND purpose = ? AND consumed_at IS NULL
                """, userId, purpose.databaseValue());
    }

    // Vô hiệu hóa các challenge còn sử dụng được của tài khoản.
    public void invalidateAll(long userId) {
        database.update("""
                UPDATE shop.verification_challenges SET consumed_at = now()
                WHERE user_id = ? AND consumed_at IS NULL
                """, userId);
    }

    // Ghi challengeId, mục đích, đích gửi, digest OTP và hạn dùng vào bảng xác minh.
    public String create(long userId, String destination, VerificationPurpose purpose,
                         String challengeId, String signedOtp, int lifetimeSeconds) {
        return (String) database.findOne("""
                INSERT INTO shop.verification_challenges
                    (user_id, destination, purpose, challenge_id, token_hash, expires_at)
                VALUES (?, ?, ?, ?, ?, now() + (? * interval '1 second'))
                RETURNING expires_at AS "expiresAt"
                """, userId, destination, purpose.databaseValue(), challengeId, signedOtp, lifetimeSeconds)
                .get("expiresAt");
    }

    // Đọc chủ sở hữu challenge trước bước xác minh có khóa.
    public Optional<Long> findOwner(String challengeId, VerificationPurpose purpose) {
        return database.findOptional("""
                SELECT user_id FROM shop.verification_challenges
                WHERE challenge_id = ? AND purpose = ?
                """, challengeId, purpose.databaseValue())
                .map(row -> ((Number) row.get("user_id")).longValue());
    }

    // Lấy và khóa hàng challenge để các lần xác minh không tiêu thụ đồng thời.
    public VerificationChallenge findForUpdate(String challengeId, VerificationPurpose purpose) {
        var row = database.findOne("""
                SELECT id, user_id, destination, challenge_id, token_hash, attempts,
                       expires_at > now() AS live, consumed_at IS NOT NULL AS consumed
                FROM shop.verification_challenges
                WHERE challenge_id = ? AND purpose = ? FOR UPDATE
                """, challengeId, purpose.databaseValue());
        return new VerificationChallenge(((Number) row.get("id")).longValue(),
                ((Number) row.get("user_id")).longValue(), row.get("destination").toString(),
                row.get("challenge_id").toString(), row.get("token_hash").toString(),
                ((Number) row.get("attempts")).intValue(), (Boolean) row.get("live"),
                (Boolean) row.get("consumed"));
    }

    // Tăng số lần thử mã OTP thất bại.
    public void incrementAttempts(long challengeId) {
        database.update("UPDATE shop.verification_challenges SET attempts = attempts + 1 WHERE id = ?", challengeId);
    }

    // Đánh dấu challenge đã được dùng thành công.
    public void consume(long challengeId) {
        database.update("UPDATE shop.verification_challenges SET consumed_at = now() WHERE id = ?", challengeId);
    }
}
