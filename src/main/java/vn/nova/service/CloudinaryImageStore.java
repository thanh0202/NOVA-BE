package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Gửi ảnh từ BE tới Cloudinary bằng HTTPS và thông tin xác thực phía server; chỉ chấp nhận URL tài nguyên đúng cloud/publicId.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.nova.exception.ApiException;

/** Bí mật chỉ tồn tại phía server; đích HTTP cố định và không theo redirect. */
@Service
public class CloudinaryImageStore {
    private final String cloud, key, secret;
    private final ObjectMapper json;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public CloudinaryImageStore(@Value("${nova.cloudinary.cloud-name:}") String cloud,
            @Value("${nova.cloudinary.api-key:}") String key, @Value("${nova.cloudinary.api-secret:}") String secret,
            ObjectMapper json) {
        this.cloud=cloud.trim(); this.key=key.trim(); this.secret=secret.trim(); this.json=json;
    }

    // Kiểm tra cloud name, API key và API secret sẵn sàng trước khi gọi Cloudinary.
    public void requireConfigured() {
        if (!cloud.matches("[A-Za-z0-9_-]+") || !key.matches("[0-9]+") || secret.isBlank())
            throw new ApiException(503,"IMAGE_STORAGE_UNAVAILABLE","Chức năng tải ảnh chưa được cấu hình. Vui lòng thử lại sau.");
    }

    /** Chỉ tái sử dụng URL gốc của đúng tài nguyên thuộc tài khoản Cloudinary đang cấu hình. */
    // Kiểm tra URL HTTPS thuộc đúng Cloudinary cloud/publicId gốc; không chấp nhận URL chuyển hướng hoặc đích khác.
    public boolean ownsAsset(String url, String publicId) {
        if (url == null || publicId == null || !cloud.matches("[A-Za-z0-9_-]+")
                || !publicId.matches("nova-app/(products|avatars)/[0-9]+/[a-f0-9]{64}")) return false;
        try {
            URI asset = URI.create(url);
            if (!"https".equalsIgnoreCase(asset.getScheme()) || !"res.cloudinary.com".equalsIgnoreCase(asset.getHost())
                    || asset.getRawUserInfo() != null || (asset.getPort() != -1 && asset.getPort() != 443)
                    || asset.getRawQuery() != null || asset.getRawFragment() != null) return false;
            String path = asset.getRawPath();
            return path != null && path.matches("/" + Pattern.quote(cloud) + "/image/upload/(?:v[0-9]+/)?"
                    + Pattern.quote(publicId) + "\\.(?:jpg|jpeg|png|webp)");
        } catch (IllegalArgumentException invalid) {
            return false;
        }
    }

    // Gửi ảnh đã được kiểm tra đến điểm nhận upload và chỉ nhận URL trả về hợp lệ.
    public String upload(String publicId, ImageUploadValidator.Image image) {
        requireConfigured();
        if (!publicId.matches("nova-app/(products|avatars)/[0-9]+/[a-f0-9]{64}")) throw new IllegalArgumentException("Invalid asset key");
        String boundary="NOVA"+UUID.randomUUID().toString().replace("-","");
        try {
            var body=new ByteArrayOutputStream(image.bytes().length+1024);
            field(body,boundary,"public_id",publicId);
            String[] scope=publicId.split("/");
            field(body,boundary,"asset_folder","Nova/app/"+(scope[1].equals("products")?"san-pham/":"avatar/")+scope[2]);
            field(body,boundary,"overwrite","false");
            field(body,boundary,"unique_filename","false");
            body.write(("--"+boundary+"\r\nContent-Disposition: form-data; name=\"file\"; filename=\"image."+image.format()+"\"\r\nContent-Type: image/"+(image.format().equals("jpg")?"jpeg":image.format())+"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(image.bytes());
            body.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
            // Basic credential được tạo trong BE từ biến môi trường; không trả key/secret cho Android.
            String credentials=Base64.getEncoder().encodeToString((key+":"+secret).getBytes(StandardCharsets.UTF_8));
            var request=HttpRequest.newBuilder(URI.create("https://api.cloudinary.com/v1_1/"+cloud+"/image/upload"))
                    .timeout(Duration.ofSeconds(40)).header("Authorization","Basic "+credentials)
                    .header("Content-Type","multipart/form-data; boundary="+boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray())).build();
            var response=client.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode()==400 || response.statusCode()==415)
                throw ApiException.badRequest("Không xử lý được ảnh. Hãy thử ảnh JPG, PNG hoặc WebP khác.");
            if (response.statusCode()<200 || response.statusCode()>299) throw unavailable();
            var result=json.readTree(response.body());
            String url=result.path("secure_url").asText("");
            if (!publicId.equals(result.path("public_id").asText()) || !ownsAsset(url,publicId)) throw unavailable();
            return url;
        } catch (InterruptedException error) { Thread.currentThread().interrupt(); throw unavailable(); }
        catch (IOException | IllegalArgumentException error) { throw unavailable(); }
    }

    // Ghi một phần trường văn bản vào body multipart gửi Cloudinary.
    private static void field(ByteArrayOutputStream body,String boundary,String name,String value) throws IOException {
        body.write(("--"+boundary+"\r\nContent-Disposition: form-data; name=\""+name+"\"\r\n\r\n"+value+"\r\n").getBytes(StandardCharsets.UTF_8));
    }
    // Tạo lỗi API khi dịch vụ ngoài chưa cấu hình hoặc chưa trả kết quả hợp lệ.
    private static ApiException unavailable() { return new ApiException(502,"IMAGE_UPLOAD_FAILED","Chưa tải được ảnh lên kho lưu trữ. Vui lòng thử lại."); }
}
