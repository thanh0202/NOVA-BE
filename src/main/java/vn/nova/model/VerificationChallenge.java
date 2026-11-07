package vn.nova.model;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Dữ liệu challenge OTP: chủ sở hữu, đích gửi, mục đích, hạn dùng và trạng thái kiểm tra.
 */
/** OTP chỉ tồn tại ở dạng HMAC; live được PostgreSQL tính theo thời gian của CSDL. */
public record VerificationChallenge(long id, long userId, String destination,
                                     String challengeId, String tokenHash,
                                     int attempts, boolean live, boolean consumed) {
}
