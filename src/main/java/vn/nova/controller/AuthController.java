package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các HTTP endpoint đăng ký, đăng nhập, OTP, khôi phục mật khẩu, kiểm tra phiên và đăng xuất; chuyển DTO sang AuthService.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.nova.dto.MessageResponse;
import vn.nova.dto.auth.AuthResponse;
import vn.nova.dto.auth.ChallengeResponse;
import vn.nova.dto.auth.EmailRequest;
import vn.nova.dto.auth.LoginRequest;
import vn.nova.dto.auth.RegisterRequest;
import vn.nova.dto.auth.ResetPasswordRequest;
import vn.nova.dto.auth.SessionResponse;
import vn.nova.dto.auth.VerifyCodeRequest;
import vn.nova.model.VerificationPurpose;
import vn.nova.service.AuthService;

/** Nhận HTTP/JSON, kiểm tra DTO rồi chuyển nghiệp vụ xuống AuthService. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    // Chuẩn hóa dữ liệu đăng ký, tạo tài khoản/challenge OTP hoặc chuyển yêu cầu tới service tương ứng.
    public ChallengeResponse register(@Valid @RequestBody RegisterRequest body, HttpServletRequest request) {
        return auth.register(body, request.getRemoteAddr());
    }

    @PostMapping("/login")
    // Kiểm tra thông tin đăng nhập và cấp phản hồi phiên sau khi tài khoản đủ điều kiện.
    public AuthResponse login(@Valid @RequestBody LoginRequest body, HttpServletRequest request) {
        return auth.login(body, request.getRemoteAddr());
    }

    @PostMapping("/verify-email")
    // Xác minh challenge OTP đăng ký rồi cập nhật trạng thái email và cấp phiên.
    public AuthResponse verifyEmail(@Valid @RequestBody VerifyCodeRequest body) {
        return auth.verifyEmail(body);
    }

    @PostMapping("/resend-verification")
    // Yêu cầu phát lại challenge xác minh email theo giới hạn tần suất.
    public ChallengeResponse resend(@Valid @RequestBody EmailRequest body, HttpServletRequest request) {
        return auth.requestCode(body, VerificationPurpose.EMAIL_VERIFY, request.getRemoteAddr());
    }

    @PostMapping("/forgot-password")
    // Bắt đầu luồng khôi phục mật khẩu bằng challenge gửi tới email.
    public ChallengeResponse forgotPassword(@Valid @RequestBody EmailRequest body, HttpServletRequest request) {
        return auth.requestCode(body, VerificationPurpose.PASSWORD_RESET, request.getRemoteAddr());
    }

    @PostMapping("/verify-reset-code")
    // Kiểm tra OTP khôi phục trước khi cho phép chuyển sang đặt mật khẩu mới.
    public MessageResponse verifyResetCode(@Valid @RequestBody VerifyCodeRequest body) {
        return auth.verifyResetCode(body);
    }

    @PostMapping("/reset-password")
    // Xác minh challenge, lưu hash mật khẩu mới và thu hồi các phiên cũ.
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest body) {
        return auth.resetPassword(body);
    }

    @GetMapping("/session")
    // Đọc hồ sơ của người dùng từ token hợp lệ để xác minh phiên hiện tại.
    public SessionResponse currentSession(@RequestHeader(value = "Authorization", required = false) String token) {
        return auth.currentSession(token);
    }

    @PostMapping("/logout")
    // Thu hồi phiên hiện tại và trả thông báo đăng xuất.
    public MessageResponse logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return auth.logout(token);
    }
}
