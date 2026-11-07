package vn.nova.dto.user;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO hồ sơ công khai trả về FE; không đưa hash mật khẩu hay thông tin xác thực server ra ngoài.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
/** Hồ sơ công khai với chính chủ; không bao giờ đưa password_hash vào response. */
public record UserProfileResponse(long id, String email, String name, String phone,
                                  String avatarUrl, String gender, String birthDate, String status,
                                  boolean emailVerified, String emailVerifiedAt,
                                  String createdAt, String role) {
}
