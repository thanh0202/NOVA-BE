package vn.nova.dto.user;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào email mới và mật khẩu xác nhận khi bắt đầu đổi email.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Giữ tên trường password của REST API để tương thích frontend hiện có. */
public record ChangeEmailRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 128) String password) {
}
