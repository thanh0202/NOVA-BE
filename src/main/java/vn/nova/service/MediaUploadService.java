package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Xác thực người tải ảnh, kiểm tra giới hạn ảnh, chống trùng bằng SHA-256, tải Cloudinary rồi ghi URL vào Neon trong luồng giao dịch.
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import vn.nova.dto.user.UserProfileResponse;
import vn.nova.exception.ApiException;
import vn.nova.repository.AdminRepository;
import vn.nova.repository.UserRepository;
import vn.nova.security.RateLimiter;

/** Giữ khóa hàng cho toàn bộ upload + ghi DB để hai lần thêm ảnh không vượt giới hạn. */
@Service
public class MediaUploadService {
    private final ImageUploadValidator validator;
    private final CloudinaryImageStore store;
    private final SessionService sessions;
    private final UserRepository users;
    private final AdminRepository admin;
    private final TransactionTemplate transactions;
    private final RateLimiter limits;
    // VPS dùng heap 256 MB: tránh nhiều request 50 MB cùng giải mã trong bộ nhớ.
    private final Semaphore uploadSlot=new Semaphore(1);

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public MediaUploadService(ImageUploadValidator validator,CloudinaryImageStore store,SessionService sessions,
            UserRepository users,AdminRepository admin,TransactionTemplate transactions,RateLimiter limits) {
        this.validator=validator; this.store=store; this.sessions=sessions; this.users=users;
        this.admin=admin; this.transactions=transactions; this.limits=limits;
    }

    // Xác thực người dùng rồi xử lý ảnh đại diện được gửi bằng multipart.
    public UserProfileResponse avatar(String authorization,MultipartFile file) {
        return bounded(() -> avatarInternal(authorization,file));
    }

    // Kiểm tra ảnh, khóa hàng tài khoản, tái dùng ảnh đã có hoặc tải ảnh mới rồi lưu avatar_url.
    private UserProfileResponse avatarInternal(String authorization,MultipartFile file) {
        long userId=sessions.requireUserId(authorization);
        limits.check("avatar-upload:"+userId,20,3600);
        var image=validator.validate(file);
        store.requireConfigured();
        return transactions.execute(tx -> {
            if (!"active".equals(users.findByIdForUpdate(userId).status())) throw ApiException.unauthorized();
            // Địa chỉ tài nguyên theo userId + SHA giúp thử lại cùng nội dung không sinh nhiều ảnh trùng.
            String publicId="nova-app/avatars/"+userId+"/"+image.sha256();
            var before=users.findProfile(userId);
            if (store.ownsAsset(before.avatarUrl(),publicId)) return before;
            String url=store.upload(publicId,image);
            users.updateAvatar(userId,url);
            return users.findProfile(userId);
        });
    }

    // Kiểm tra yêu cầu và điều phối thêm nhiều ảnh sản phẩm cho ADMIN.
    public Map<String,Object> productImages(long actor,long productId,List<MultipartFile> files) {
        return bounded(() -> productImagesInternal(actor,productId,files));
    }

    // Khóa sản phẩm, loại ảnh trùng, kiểm tra số chỗ còn lại, tải Cloudinary và ghi URL theo thứ tự.
    private Map<String,Object> productImagesInternal(long actor,long productId,List<MultipartFile> files) {
        if (!admin.isAdmin(actor)) throw new ApiException(403,"ADMIN_REQUIRED","Tài khoản chưa được cấp quyền quản trị.");
        if (files==null || files.isEmpty() || files.size()>10) throw ApiException.badRequest("Chọn từ 1 đến 10 ảnh mỗi lần.");
        limits.check("product-image-upload:"+actor,80,3600);
        // Loại ảnh trùng trong cùng lần gửi trước khi tính số chỗ còn lại.
        var unique=new LinkedHashMap<String,ImageUploadValidator.Image>();
        for (var file:files) { var image=validator.validate(file); unique.putIfAbsent(image.sha256(),image); }
        store.requireConfigured();
        return transactions.execute(tx -> {
            admin.lockProduct(productId);
            var current=admin.images(productId);
            var pending=new ArrayList<ImageUploadValidator.Image>();
            unique.values().forEach(image -> {
                String publicId="nova-app/products/"+productId+"/"+image.sha256();
                if (current.stream().noneMatch(existing -> store.ownsAsset(String.valueOf(existing.get("url")),publicId))) pending.add(image);
            });
            int cap=admin.imageLimit(productId);
            if (current.size()+pending.size()>cap) throw new ApiException(409,"IMAGE_LIMIT_REACHED",
                    "Sản phẩm đã có "+current.size()+" ảnh. Nhóm này được tối đa "+cap+" ảnh.");
            String name=String.valueOf(admin.product(productId).get("name"));
            for (var image:pending) {
                String url=store.upload("nova-app/products/"+productId+"/"+image.sha256(),image);
                // CSDL chỉ lưu URL được Cloudinary trả về; byte ảnh nằm ở dịch vụ lưu trữ ảnh.
                admin.appendImage(productId,url,name);
            }
            return admin.product(productId);
        });
        // Nếu DB rollback sau khi Cloudinary lưu ảnh, ID theo SHA cho phép lần thử lại dùng lại
        // chính tài nguyên đó. Không xóa tài nguyên có thể đã được request khác sử dụng.
    }

    // Giới hạn một tác vụ upload cùng lúc và luôn nhả Semaphore trong finally.
    private <T> T bounded(Supplier<T> action) {
        if (!uploadSlot.tryAcquire()) throw new ApiException(429,"UPLOAD_BUSY","Máy chủ đang tải ảnh khác. Vui lòng thử lại sau ít giây.");
        try { return action.get(); } finally { uploadSlot.release(); }
    }
}
