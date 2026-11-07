package vn.nova.dto.user;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO đầu vào tên, điện thoại, ngày sinh, giới tính và URL avatar tùy chọn khi cập nhật hồ sơ.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import jakarta.validation.constraints.Size;

/** Các trường được phép sửa; trường lạ như role/email/status bị Jackson từ chối. */
public record UpdateProfileRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 20) String phone,
        @Size(max = 2000) String avatarUrl,
        @Size(max = 20) String gender,
        @PastOrPresent LocalDate birthDate) {
}
