package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào đăng ký gồm email, mật khẩu, tên và điện thoại cùng các ràng buộc hợp lệ.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu đăng ký; role và trạng thái tài khoản do server quyết định. */
public record RegisterRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 20) String phone) {
}
