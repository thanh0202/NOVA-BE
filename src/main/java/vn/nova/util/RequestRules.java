package vn.nova.util;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chuẩn hóa email/số điện thoại và chuyển chuỗi trống thành null trước khi xử lý nghiệp vụ.
 */
import java.util.Locale;
import vn.nova.exception.ApiException;

/** Các quy tắc chuẩn hóa dữ liệu dùng chung, độc lập với HTTP và CSDL. */
public final class RequestRules {
    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    private RequestRules() {
    }

    // Cắt khoảng trắng và chuẩn hóa email trước khi lưu/tìm kiếm.
    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Chuẩn hóa và kiểm tra định dạng số điện thoại.
    public static String normalizePhone(String value) {
        String phone = value == null ? "" : value.trim();
        if (!phone.isEmpty() && !phone.matches("[+0-9() -]{8,20}")) {
            throw ApiException.badRequest("Số điện thoại chưa hợp lệ.");
        }
        return phone;
    }

    // Đổi chuỗi trống thành null để ghi các trường tùy chọn.
    public static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
