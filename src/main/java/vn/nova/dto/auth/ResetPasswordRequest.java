package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào đặt mật khẩu mới bằng challengeId và mã OTP.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Đặt mật khẩu mới bằng OTP của luồng quên mật khẩu. */
public record ResetPasswordRequest(
        @NotBlank @Size(max = 64) String challengeId,
        @NotBlank @Pattern(regexp = "[0-9]{6}") String code,
        @NotBlank @Size(min = 8, max = 128) String newPassword) {
}
