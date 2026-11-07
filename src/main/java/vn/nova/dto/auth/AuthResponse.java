package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO trả token đăng nhập, loại Bearer, thời hạn, thời điểm hết hạn và hồ sơ người dùng.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import vn.nova.dto.user.UserProfileResponse;

/** Token phiên opaque có thể thu hồi; frontend gửi lại dưới dạng {@code Authorization: Bearer TOKEN}. */
public record AuthResponse(String token, String tokenType, int expiresIn,
                           String expiresAt, UserProfileResponse user) {
}
