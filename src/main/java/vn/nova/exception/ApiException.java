package vn.nova.exception;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lỗi nghiệp vụ mang HTTP status, mã lỗi và thông báo có thể trả về cho client.
 */
/** Lỗi nghiệp vụ được chuyển thành HTTP status + code + message bởi ApiExceptionHandler. */
public class ApiException extends RuntimeException {
    private final int status;
    private final String code;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    // Trả HTTP status được gắn với lỗi nghiệp vụ.
    public int getStatus() {
        return status;
    }

    // Trả mã lỗi ổn định để client nhận diện trường hợp thất bại.
    public String getCode() {
        return code;
    }

    // Tạo lỗi yêu cầu không hợp lệ với HTTP status 400.
    public static ApiException badRequest(String message) {
        return new ApiException(400, "VALIDATION_ERROR", message);
    }

    // Tạo lỗi 404 khi đối tượng được yêu cầu không tồn tại.
    public static ApiException notFound() {
        return new ApiException(404, "NOT_FOUND", "Không tìm thấy dữ liệu.");
    }

    // Tạo lỗi 401 khi thiếu hoặc không còn phiên xác thực hợp lệ.
    public static ApiException unauthorized() {
        return new ApiException(401, "UNAUTHORIZED", "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
    }
}
