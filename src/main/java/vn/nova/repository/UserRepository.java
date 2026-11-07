package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các truy vấn JDBC cho tài khoản và hồ sơ: tạo, khóa hàng để cập nhật, đổi email/mật khẩu và lưu URL avatar.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import vn.nova.dto.user.UserProfileResponse;
import vn.nova.model.AccountRecord;
import vn.nova.util.JdbcExecutor;

/** Tất cả SQL đọc/ghi tài khoản và hồ sơ được tập trung tại đây. */
@Repository
public class UserRepository {
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public UserRepository(JdbcExecutor database) {
        this.database = database;
    }

    // Kiểm tra có tài khoản đã dùng email được chuẩn hóa hay chưa.
    public boolean emailExists(String email) {
        return database.findLong("SELECT count(*) FROM shop.users WHERE email = ?", email) > 0;
    }

    // Chèn tài khoản với hash mật khẩu và thông tin đăng ký.
    public long createAccount(String email, String passwordHash, String name, String phone) {
        return database.findLong("""
                INSERT INTO shop.users(email, password_hash, full_name, phone)
                VALUES (?, ?, ?, ?) RETURNING id
                """, email, passwordHash, name, phone);
    }

    /** Tạo dữ liệu liên quan trong cùng transaction đăng ký, tương thích schema NOVA. */
    // Tạo các dữ liệu mặc định liên quan cho tài khoản vừa đăng ký.
    public void createAccountDefaults(long userId) {
        database.update("""
                INSERT INTO shop.user_roles(user_id, role_id)
                SELECT ?, id FROM shop.roles WHERE code = 'CUSTOMER'
                """, userId);
        database.update("INSERT INTO shop.carts(user_id) VALUES (?)", userId);
        database.update("INSERT INTO shop.notification_preferences(user_id) VALUES (?)", userId);
        database.update("""
                INSERT INTO shop.membership_accounts(user_id, tier_id)
                SELECT ?, id FROM shop.membership_tiers ORDER BY rank LIMIT 1
                """, userId);
    }

    /** FOR UPDATE được gọi bên trong transaction khi đăng nhập, gửi lại OTP hoặc đổi thông tin. */
    // Đọc và khóa hàng tài khoản theo email trong giao dịch.
    public Optional<AccountRecord> findByEmailForUpdate(String email) {
        return database.findOptional("""
                SELECT id, email, password_hash, status, email_verified_at
                FROM shop.users WHERE email = ? FOR UPDATE
                """, email).map(this::account);
    }

    // Đọc và khóa hàng tài khoản theo ID trong giao dịch.
    public AccountRecord findByIdForUpdate(long userId) {
        return account(database.findOne("""
                SELECT id, email, password_hash, status, email_verified_at
                FROM shop.users WHERE id = ? FOR UPDATE
                """, userId));
    }

    // Đánh dấu email đã được xác minh khi challenge hợp lệ.
    public void markEmailVerified(long userId, String email) {
        database.update("""
                UPDATE shop.users SET email_verified_at = now(), updated_at = now()
                WHERE id = ? AND email = ?
                """, userId, email);
    }

    // Cập nhật hash mật khẩu mới của tài khoản.
    public void updatePassword(long userId, String passwordHash) {
        database.update("""
                UPDATE shop.users
                SET password_hash = ?, password_changed_at = now(), updated_at = now()
                WHERE id = ?
                """, passwordHash, userId);
    }

    // Cập nhật email sau khi hoàn tất xác minh.
    public void updateEmail(long userId, String email) {
        database.update("""
                UPDATE shop.users SET email = ?, email_verified_at = now(), updated_at = now()
                WHERE id = ?
                """, email, userId);
    }

    // Kiểm tra dữ liệu hồ sơ và cập nhật các trường hợp lệ trong giao dịch.
    public void updateProfile(long userId, String name, String phone, String gender, boolean changeAvatar, String avatarUrl, String birthDate) {
        database.update("""
                UPDATE shop.users
                SET full_name = ?, phone = ?, gender = ?, avatar_url = CASE WHEN ? THEN ? ELSE avatar_url END,
                    birth_date = ?::date, updated_at = now()
                WHERE id = ?
                """, name, phone, gender, changeAvatar, avatarUrl, birthDate, userId);
    }

    // Lưu URL ảnh đại diện vào users.avatar_url.
    public void updateAvatar(long userId,String url) {
        database.update("UPDATE shop.users SET avatar_url=?,updated_at=now() WHERE id=?",url,userId);
    }

    // Ghép các trường hồ sơ thành UserProfileResponse.
    public UserProfileResponse findProfile(long userId) {
        Map<String, Object> row = database.findOne("""
                SELECT id, email, full_name AS name, phone, avatar_url AS "avatarUrl",
                       gender, birth_date AS "birthDate", status, email_verified_at AS "emailVerifiedAt", created_at AS "createdAt"
                FROM shop.users WHERE id = ?
                """, userId);
        String role = database.findOptional("""
                SELECT r.code FROM shop.user_roles ur
                JOIN shop.roles r ON r.id = ur.role_id WHERE ur.user_id = ?
                ORDER BY CASE r.code WHEN 'ADMIN' THEN 0 WHEN 'SUPPORT' THEN 1 ELSE 2 END
                LIMIT 1
                """, userId).map(value -> text(value, "code")).orElse("CUSTOMER");
        return new UserProfileResponse(userId, text(row, "email"), text(row, "name"),
                text(row, "phone"), text(row, "avatarUrl"), text(row, "gender"), text(row, "birthDate"),
                text(row, "status"), row.get("emailVerifiedAt") != null,
                text(row, "emailVerifiedAt"), text(row, "createdAt"), role);
    }

    // Ánh xạ một dòng ResultSet tài khoản sang AccountRecord.
    private AccountRecord account(Map<String, Object> row) {
        return new AccountRecord(((Number) row.get("id")).longValue(), text(row, "email"),
                text(row, "password_hash"), text(row, "status"), text(row, "email_verified_at"));
    }

    // Đọc cột văn bản tùy chọn của ResultSet để ánh xạ hồ sơ.
    private String text(Map<String, Object> row, String column) {
        Object value = row.get(column);
        return value == null ? null : value.toString();
    }
}
