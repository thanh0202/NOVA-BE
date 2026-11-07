package vn.nova.model;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Dữ liệu tài khoản đọc từ SQL phục vụ xác thực, gồm trạng thái, vai trò, hash mật khẩu và email đã xác minh.
 */
/** Dữ liệu nội bộ cho xác thực; không trả record này trực tiếp qua controller. */
public record AccountRecord(long id, String email, String passwordHash,
                             String status, String emailVerifiedAt) {
    // Trả true khi trạng thái tài khoản là active.
    public boolean active() {
        return "active".equals(status);
    }

    // Kiểm tra cờ/thời điểm email đã được xác minh của tài khoản.
    public boolean emailVerified() {
        return emailVerifiedAt != null;
    }
}
