package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các endpoint hồ sơ, cập nhật hồ sơ, đổi mật khẩu và đổi email; xác định người dùng qua Bearer token.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nova.dto.MessageResponse;
import vn.nova.dto.auth.ChallengeResponse;
import vn.nova.dto.auth.VerifyCodeRequest;
import vn.nova.dto.user.ChangeEmailRequest;
import vn.nova.dto.user.ChangePasswordRequest;
import vn.nova.dto.user.UpdateProfileRequest;
import vn.nova.dto.user.UserProfileResponse;
import vn.nova.service.UserService;

/** Các API của chính tài khoản đăng nhập; giữ alias /users/me cho client hiện có. */
@RestController
@RequestMapping({"/api/me", "/api/users/me"})
public class UserController {
    private final UserService users;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    // Trả hồ sơ hiện tại sau khi xác thực người gọi.
    public UserProfileResponse profile(@RequestHeader(value = "Authorization", required = false) String token) {
        return users.getProfile(token);
    }

    @PutMapping
    // Nhận yêu cầu cập nhật và chuyển dữ liệu đã validation xuống tầng service.
    public UserProfileResponse update(@RequestHeader(value = "Authorization", required = false) String token,
                                      @Valid @RequestBody UpdateProfileRequest body) {
        return users.updateProfile(token, body);
    }

    @PutMapping("/password")
    // Kiểm tra mật khẩu hiện tại, lưu hash mới và thu hồi phiên để yêu cầu đăng nhập lại.
    public MessageResponse changePassword(@RequestHeader(value = "Authorization", required = false) String token,
                                           @Valid @RequestBody ChangePasswordRequest body) {
        return users.changePassword(token, body);
    }

    @PostMapping("/email")
    // Kiểm tra email/mật khẩu và phát challenge xác minh tới email mới.
    public ChallengeResponse changeEmail(@RequestHeader(value = "Authorization", required = false) String token,
                                         @Valid @RequestBody ChangeEmailRequest body) {
        return users.changeEmail(token, body);
    }

    @PostMapping("/email/verify")
    // Tiêu thụ challenge đổi email, cập nhật địa chỉ mới và thu hồi phiên cũ.
    public MessageResponse verifyEmailChange(@RequestHeader(value = "Authorization", required = false) String token,
                                             @Valid @RequestBody VerifyCodeRequest body) {
        return users.verifyEmailChange(token, body);
    }
}
