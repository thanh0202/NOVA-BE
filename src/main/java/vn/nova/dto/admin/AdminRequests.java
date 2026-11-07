package vn.nova.dto.admin;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Tập DTO record đầu vào quản trị; annotation Jakarta Validation mô tả các ràng buộc của từng trường.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/** Dữ liệu quản trị được khai báo tường minh; không nhận SQL hoặc tên bảng từ client. */
public final class AdminRequests {
    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    private AdminRequests() { }

    public record Variant(Long id,
            @NotBlank @Size(max = 100) String sku,
            @NotBlank @Size(max = 100) String color,
            @Min(1) @Max(16384) Integer storageGb,
            @Min(1) @Max(1024) Integer ramGb,
            @Min(0) @Max(1000000000000L) long price,
            @Min(0) @Max(1000000000000L) Long originalPrice,
            @NotBlank @Pattern(regexp = "active|inactive") String status) { }

    public record Product(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,80}") String code,
            @NotBlank @Size(max = 250) String name,
            @NotBlank @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") @Size(max = 300) String slug,
            @Size(max = 3000) String summary,
            @Size(max = 30000) String description,
            @Min(1) long brandId, @Min(1) long categoryId,
            @NotBlank @Pattern(regexp = "active|draft|archived") String status,
            boolean featured, @Size(max = 2048) String imageUrl,
            @NotNull @Size(min = 1, max = 60) List<@NotNull @Valid Variant> variants) { }

    public record Category(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,60}") String code,
            @NotBlank @Size(max = 120) String name,
            @Min(1) Long parentId, @Min(0) @Max(100000) int sortOrder,
            boolean active, @Size(max = 2048) String imageUrl) { }

    public record Brand(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,60}") String code,
            @NotBlank @Size(max = 100) String name,
            boolean active, @Size(max = 2048) String logoUrl) { }

    public record Banner(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 2048) String imageUrl,
            @Min(1) Long productId, boolean active) { }

    public record UserStatus(@NotBlank @Pattern(regexp = "active|locked|disabled") String status) { }
}
