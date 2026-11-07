package vn.nova.model;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Dữ liệu token phiên vừa cấp kèm thời điểm hết hạn.
 */
/** Token gốc chỉ được dùng để tạo response cho client; CSDL lưu digest thay cho token gốc. */
public record SessionToken(String token, String expiresAt) {
}
