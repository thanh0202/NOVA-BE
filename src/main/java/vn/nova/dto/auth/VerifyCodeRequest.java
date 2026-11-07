package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào challengeId và mã OTP để xác minh đúng yêu cầu.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** challengeId được trả khi gửi OTP; code là sáu chữ số người dùng nhận trong email. */
public record VerifyCodeRequest(
        @NotBlank @Size(max = 64) String challengeId,
        @NotBlank @Pattern(regexp = "[0-9]{6}") String code) {
}
