package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO trả thông tin challenge OTP và thông báo; không chứa mã OTP thuần.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import com.fasterxml.jackson.annotation.JsonInclude;

/** Response tuyệt đối không chứa mã OTP. Thời gian được tính bằng giây. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChallengeResponse(String challengeId, String expiresAt, int expiresIn,
                                int resendAfter, String message) {
    // Tạo bản sao DTO có thông báo mới, giữ nguyên dữ liệu challenge.
    public ChallengeResponse withMessage(String newMessage) {
        return new ChallengeResponse(challengeId, expiresAt, expiresIn, resendAfter, newMessage);
    }
}
