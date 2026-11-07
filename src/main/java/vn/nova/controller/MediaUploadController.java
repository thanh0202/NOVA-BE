package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Nhận multipart ảnh đại diện hoặc ảnh sản phẩm; chuyển việc kiểm tra, tải Cloudinary và lưu URL cho MediaUploadService.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.nova.dto.user.UserProfileResponse;
import vn.nova.service.MediaUploadService;

@RestController
public class MediaUploadController {
    private final MediaUploadService media;
    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public MediaUploadController(MediaUploadService media) { this.media=media; }

    @PostMapping(value={"/api/me/avatar","/api/users/me/avatar"},consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // Xác thực người dùng rồi xử lý ảnh đại diện được gửi bằng multipart.
    public UserProfileResponse avatar(@RequestHeader(value="Authorization",required=false) String authorization,
            @RequestPart("file") MultipartFile file) { return media.avatar(authorization,file); }

    /** Interceptor /api/admin/** xác thực ADMIN trước khi controller được thực thi. */
    @PostMapping(value="/api/admin/products/{id}/images",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // Kiểm tra yêu cầu và điều phối thêm nhiều ảnh sản phẩm cho ADMIN.
    public Map<String,Object> productImages(@RequestAttribute("nova.adminId") long actor,@PathVariable long id,
            @RequestPart("files") List<MultipartFile> files) { return media.productImages(actor,id,files); }
}
