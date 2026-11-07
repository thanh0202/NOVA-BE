package vn.nova.security;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu.
 */
/** Quy tắc cho mật khẩu mới; không áp dụng lại khi kiểm tra mật khẩu đã lưu. */
public final class PasswordRules {
    public static final String MESSAGE =
            "Mật khẩu từ 8–128 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt.";

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    private PasswordRules() {}

    // Kiểm tra các quy tắc độ dài và nhóm ký tự bắt buộc của mật khẩu mới.
    public static boolean isValid(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (int index = 0; index < password.length();) {
            int code = password.codePointAt(index);
            index += Character.charCount(code);
            upper |= Character.isUpperCase(code);
            lower |= Character.isLowerCase(code);
            digit |= Character.isDigit(code);
            // Khoảng trắng và dấu kết hợp không được tính là ký tự đặc biệt.
            special |= switch (Character.getType(code)) {
                case Character.CONNECTOR_PUNCTUATION, Character.DASH_PUNCTUATION,
                     Character.START_PUNCTUATION, Character.END_PUNCTUATION,
                     Character.INITIAL_QUOTE_PUNCTUATION, Character.FINAL_QUOTE_PUNCTUATION,
                     Character.OTHER_PUNCTUATION, Character.MATH_SYMBOL,
                     Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL,
                     Character.OTHER_SYMBOL -> true;
                default -> false;
            };
        }
        return upper && lower && digit && special;
    }
}
