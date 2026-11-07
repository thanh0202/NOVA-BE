package vn.nova.dto.user;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào mật khẩu hiện tại và mật khẩu mới để thay mật khẩu.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Đổi mật khẩu khi đã đăng nhập; các phiên cũ sẽ bị thu hồi. */
public record ChangePasswordRequest(
        @NotBlank @Size(max = 128) String currentPassword,
        @NotBlank @Size(min = 8, max = 128) String newPassword) {
}
