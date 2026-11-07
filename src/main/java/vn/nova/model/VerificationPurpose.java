package vn.nova.model;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Liệt kê các mục đích xác minh email/khôi phục mật khẩu/đổi email và mã lưu tương ứng trong CSDL.
 */
/** Các giá trị tương ứng với purpose trong shop.verification_challenges. */
public enum VerificationPurpose {
    EMAIL_VERIFY("email_verify"),
    PASSWORD_RESET("password_reset"),
    EMAIL_CHANGE("email_change");

    private final String databaseValue;

    VerificationPurpose(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    // Trả mã mục đích OTP dùng khi ghi vào CSDL.
    public String databaseValue() {
        return databaseValue;
    }
}
