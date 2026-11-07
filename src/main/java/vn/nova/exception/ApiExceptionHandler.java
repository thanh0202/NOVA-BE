package vn.nova.exception;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chuyển lỗi nghiệp vụ, validation, xung đột dữ liệu, tệp quá lớn và JDBC thành phản hồi HTTP JSON thống nhất.
 */
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/** Một nơi thống nhất định dạng lỗi; không đưa SQL, mật khẩu hoặc stack trace vào response. */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    // Đổi ApiException thành JSON gồm mã lỗi/thông báo cùng HTTP status tương ứng.
    public ResponseEntity<?> handleBusinessError(ApiException error) {
        return ResponseEntity.status(error.getStatus())
                .body(Map.of("code", error.getCode(), "message", error.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    // Đổi lỗi toàn vẹn/xung đột dữ liệu thành phản hồi phù hợp, không lộ câu SQL.
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException error) {
        return ResponseEntity.status(409).body(Map.of(
                "code", "DATA_CONFLICT", "message", "Thông tin đã tồn tại hoặc không hợp lệ."));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class, IllegalArgumentException.class})
    // Trả lỗi validation hoặc dữ liệu request không đọc được theo cấu trúc API.
    public ResponseEntity<?> handleInvalidInput(Exception error) {
        return ResponseEntity.badRequest().body(Map.of(
                "code", "VALIDATION_ERROR", "message", "Dữ liệu yêu cầu không hợp lệ."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    // Trả lỗi tệp vượt giới hạn multipart.
    public ResponseEntity<?> handleLargeImage(MaxUploadSizeExceededException error) {
        return ResponseEntity.status(413).body(Map.of("code","IMAGE_TOO_LARGE",
                "message","Mỗi ảnh được tối đa 5 MB; mỗi lần tải tối đa 55 MB."));
    }

    @ExceptionHandler(DataAccessException.class)
    // Trả lỗi máy chủ khi JDBC/CSDL không xử lý được, giữ chi tiết nội bộ phía BE.
    public ResponseEntity<?> handleDatabaseError(DataAccessException error) {
        // Chỉ ghi tên loại lỗi, tránh ghi tham số SQL có thể chứa dữ liệu riêng tư.
        LOG.error("Database request failed: {}", error.getClass().getSimpleName());
        return ResponseEntity.status(503).body(Map.of(
                "code", "DATABASE_UNAVAILABLE", "message", "Chưa thể xử lý dữ liệu. Vui lòng thử lại sau."));
    }
}
