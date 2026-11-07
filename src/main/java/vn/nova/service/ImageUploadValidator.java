package vn.nova.service;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Kiểm tra nội dung thật của ảnh, loại định dạng, kích thước, giới hạn dung lượng và tạo SHA-256 để nhận diện ảnh trùng.
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.nova.exception.ApiException;

/** Kiểm tra nội dung ảnh, không tin phần mở rộng hoặc MIME do client gửi. */
@Component
public class ImageUploadValidator {
    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final long MAX_PIXELS = 16_000_000L;
    public record Image(byte[] bytes, String format, int width, int height, String sha256) {}

    // Kiểm tra byte thật của JPG/PNG/WebP, giới hạn kích thước và tạo SHA-256 của nội dung để chống ảnh trùng.
    public Image validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw ApiException.badRequest("Hãy chọn ảnh để tải lên.");
        if (file.getSize() > MAX_BYTES) throw new ApiException(413, "IMAGE_TOO_LARGE", "Mỗi ảnh được tối đa 5 MB.");
        try {
            byte[] bytes;
            try (var stream = file.getInputStream()) { bytes = stream.readNBytes((int) MAX_BYTES + 1); }
            if (bytes.length > MAX_BYTES) throw new ApiException(413, "IMAGE_TOO_LARGE", "Mỗi ảnh được tối đa 5 MB.");
            String format;
            int width, height;
            if (bytes.length >= 30 && ascii(bytes,0,"RIFF") && ascii(bytes,8,"WEBP")) {
                format = "webp";
                // Cloudinary kiểm tra và giải mã WebP lần nữa trước khi lưu tài nguyên.
                if (ascii(bytes,12,"VP8X")) {
                    if ((bytes[20] & 2) != 0) throw invalid(); // Không nhận WebP động.
                    width = little24(bytes,24) + 1; height = little24(bytes,27) + 1;
                } else if (ascii(bytes,12,"VP8L") && bytes[20] == 0x2f) {
                    width = 1 + ((bytes[21]&255) | ((bytes[22]&0x3f)<<8));
                    height = 1 + (((bytes[22]&0xc0)>>6) | ((bytes[23]&255)<<2) | ((bytes[24]&15)<<10));
                } else if (ascii(bytes,12,"VP8 ") && (bytes[23]&255)==0x9d && bytes[24]==1 && bytes[25]==0x2a) {
                    width = ((bytes[26]&255) | ((bytes[27]&255)<<8)) & 0x3fff;
                    height = ((bytes[28]&255) | ((bytes[29]&255)<<8)) & 0x3fff;
                } else throw invalid();
                dimensions(width,height);
            } else {
                boolean jpeg = bytes.length > 3 && (bytes[0]&255)==255 && (bytes[1]&255)==216 && (bytes[2]&255)==255;
                boolean png = bytes.length > 8 && Arrays.equals(Arrays.copyOf(bytes,8), new byte[]{(byte)137,80,78,71,13,10,26,10});
                if (!jpeg && !png) throw invalid();
                format = jpeg ? "jpg" : "png";
                try (var input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
                    var readers = ImageIO.getImageReaders(input);
                    if (!readers.hasNext()) throw invalid();
                    var reader = readers.next();
                    try {
                        reader.setInput(input,true,true);
                        width = reader.getWidth(0); height = reader.getHeight(0);
                        dimensions(width,height);
                        // Đọc dữ liệu pixel để loại tệp hỏng chỉ có header hợp lệ.
                        var decoded = reader.read(0);
                        if (decoded == null) throw invalid();
                        decoded.flush();
                    } finally { reader.dispose(); }
                }
            }
            return new Image(bytes,format,width,height,HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
        } catch (IOException error) { throw invalid(); }
        catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }

    // Đọc số nguyên 24 bit little-endian trong phần đầu tệp WebP.
    private static int little24(byte[] bytes,int start) { return (bytes[start]&255) | ((bytes[start+1]&255)<<8) | ((bytes[start+2]&255)<<16); }
    // So sánh chữ ký byte với nhãn ASCII để nhận dạng nội dung ảnh.
    private static boolean ascii(byte[] bytes,int start,String expected) {
        if (start+expected.length()>bytes.length) return false;
        for (int i=0;i<expected.length();i++) if (bytes[start+i]!=expected.charAt(i)) return false;
        return true;
    }
    // Kiểm tra giới hạn chiều rộng, chiều cao và tổng số pixel của ảnh.
    private static void dimensions(int width,int height) {
        if (width<1 || height<1 || width>10000 || height>10000 || (long)width*height>MAX_PIXELS)
            throw ApiException.badRequest("Ảnh quá lớn. Hãy chọn ảnh tối đa 16 megapixel.");
    }
    // Tạo lỗi 400 cho dữ liệu ảnh không hợp lệ.
    private static ApiException invalid() { return ApiException.badRequest("Ảnh chưa hợp lệ. Hãy dùng ảnh JPG, PNG hoặc WebP tĩnh."); }
}
