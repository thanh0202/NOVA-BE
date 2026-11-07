package vn.nova.dto;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO phản hồi ngắn chỉ chứa thông báo cho các thao tác không cần trả đối tượng dữ liệu.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
/** Thông báo ngắn cho thao tác đã hoàn tất, ví dụ đăng xuất hoặc đổi mật khẩu. */
public record MessageResponse(String message) {
}
