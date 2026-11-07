package vn.nova.dto.auth;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO trả tình trạng phiên đã xác thực và hồ sơ hiện tại.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import vn.nova.dto.user.UserProfileResponse;

/** Kết quả kiểm tra phiên đang đăng nhập. */
public record SessionResponse(boolean authenticated, UserProfileResponse user) {
}
