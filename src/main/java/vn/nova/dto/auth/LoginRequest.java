package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào email và mật khẩu khi đăng nhập; validation chạy trước service.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Email và mật khẩu ứng dụng NOVA, không phải mật khẩu SMTP. */
public record LoginRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 128) String password) {
}
