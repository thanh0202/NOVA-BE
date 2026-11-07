package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào email, dùng cho yêu cầu gửi/gửi lại OTP.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dùng chung cho gửi lại xác thực email và quên mật khẩu. */
public record EmailRequest(@NotBlank @Email @Size(max = 254) String email) {
}
