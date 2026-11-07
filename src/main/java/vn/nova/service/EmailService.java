package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gửi email OTP qua JavaMailSender; chuyển lỗi SMTP thành lỗi API phù hợp.
 */
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.nova.exception.ApiException;
import vn.nova.model.VerificationPurpose;

/** Gửi OTP qua SMTP. API chỉ trả challengeId; OTP chỉ gửi đến email người nhận. */
@Service
public class EmailService {
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${nova.mail-from:}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    // Dựng nội dung email và gửi mã qua JavaMailSender/SMTP được cấu hình.
    public void sendOtp(String destination, String code, VerificationPurpose purpose) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || from.isBlank()) {
            throw unavailable();
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(destination);
        message.setSubject(purpose == VerificationPurpose.PASSWORD_RESET
                ? "Đặt lại mật khẩu NOVA" : "Xác thực email NOVA");
        message.setText("Mã xác thực của bạn: " + code
                + "\nMã có hiệu lực 5 phút và chỉ dùng một lần."
                + "\nNếu bạn không yêu cầu, hãy bỏ qua email này.");
        try {
            sender.send(message);
        } catch (MailException error) {
            // Không log nội dung email, OTP hoặc thông tin đăng nhập SMTP.
            throw unavailable();
        }
    }

    // Tạo lỗi API khi dịch vụ ngoài chưa cấu hình hoặc chưa trả kết quả hợp lệ.
    private ApiException unavailable() {
        return new ApiException(503, "MAIL_UNAVAILABLE", "Chưa gửi được email xác thực. Vui lòng thử lại sau.");
    }
}
