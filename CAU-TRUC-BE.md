# Cây thư mục BE NOVA — giải thích từng file

Đây là gói NOVA_BE_GITHUB độc lập. Mỗi dòng file và thư mục đều có phần `# chức năng` ngay phía sau.

**110 file · 60 Java.**

Phần BE giữ để đọc mã nguồn. [Tài liệu CSDL và ERD](docs/GIAI-THICH-CSDL.md) mô tả metadata Neon được đọc trực tiếp.

```text
NOVA_BE_GITHUB/  # Backend Spring Boot và tài liệu CSDL.
├── database/  # SQL có trong mã nguồn GK; không phải bản sao dữ liệu Neon.
│   └── 017_home_featured.sql  # Migration chọn một nhóm sản phẩm hiện có làm nổi bật; giữ dữ liệu giá, ảnh và tồn kho, không tạo lại toàn bộ CSDL.
├── docs/  # Tài liệu riêng của gói.
│   ├── csdl/  # Tài liệu metadata và các sơ đồ CSDL Neon.
│   │   ├── ERD-DON-HANG.mmd  # Mã Mermaid của sơ đồ Giỏ hàng, đơn hàng và giao nhận, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-DON-HANG.svg  # Sơ đồ ERD vector Giỏ hàng, đơn hàng và giao nhận; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-HE-THONG-HO-TRO.mmd  # Mã Mermaid của sơ đồ Thông báo, hỗ trợ và vận hành, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-HE-THONG-HO-TRO.svg  # Sơ đồ ERD vector Thông báo, hỗ trợ và vận hành; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-KHO-HANG.mmd  # Mã Mermaid của sơ đồ Kho và nhập hàng, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-KHO-HANG.svg  # Sơ đồ ERD vector Kho và nhập hàng; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-KHUYEN-MAI.mmd  # Mã Mermaid của sơ đồ Voucher và Flash Sale, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-KHUYEN-MAI.svg  # Sơ đồ ERD vector Voucher và Flash Sale; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-SAN-PHAM.mmd  # Mã Mermaid của sơ đồ Danh mục, sản phẩm và đánh giá, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-SAN-PHAM.svg  # Sơ đồ ERD vector Danh mục, sản phẩm và đánh giá; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-TAI-KHOAN.mmd  # Mã Mermaid của sơ đồ Tài khoản và xác thực, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-TAI-KHOAN.svg  # Sơ đồ ERD vector Tài khoản và xác thực; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-THANH-TOAN-DOI-TRA.mmd  # Mã Mermaid của sơ đồ Thanh toán, đổi trả và hoàn tiền, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-THANH-TOAN-DOI-TRA.svg  # Sơ đồ ERD vector Thanh toán, đổi trả và hoàn tiền; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-THANH-VIEN.mmd  # Mã Mermaid của sơ đồ Thành viên và điểm thưởng, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-THANH-VIEN.svg  # Sơ đồ ERD vector Thành viên và điểm thưởng; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── ERD-TOAN-BO.mmd  # Mã Mermaid của sơ đồ Toàn bộ 66 bảng, có đủ cột và các khóa ngoại thuộc phạm vi.
│   │   ├── ERD-TOAN-BO.svg  # Sơ đồ ERD vector Toàn bộ 66 bảng; mở trực tiếp hoặc xem trong trang HTML.
│   │   ├── SCHEMA-NEON.json  # Metadata chỉ đọc từ catalog Neon, dùng làm nguồn sinh tài liệu/ERD; không chứa hàng dữ liệu hay thông tin đăng nhập.
│   │   ├── SO-DO-CSDL.html  # Trình xem ERD độc lập/offline: chọn nhóm, kéo, tìm bảng và phóng to/thu nhỏ.
│   │   ├── SO-DO-CSDL.md  # Trang tổng hợp sơ đồ toàn bộ CSDL và từng nhóm nghiệp vụ, kèm SVG và nguồn Mermaid.
│   │   └── TU-DIEN-DU-LIEU.md  # Từ điển đủ 495 cột, PK/FK/UNIQUE/CHECK, 212 index, 15 trigger, 12 view và 17 hàm/thủ tục.
│   ├── CHUC-NANG-TUNG-FILE.md  # Bảng chức năng toàn bộ file trong gói BE.
│   ├── GIAI-THICH-CODE.md  # Giải thích luồng xử lý và cách đọc mã BE.
│   ├── GIAI-THICH-CSDL.md  # Giải thích CSDL Neon hiện có: 66 bảng, các nhóm nghiệp vụ, quan hệ, quy tắc lưu trữ và sơ đồ ERD.
│   └── TRA-CUU-HAM.md  # Tra cứu phương thức Java và liên kết tới đúng dòng mã nguồn BE.
├── gradle/  # Cấu hình và công cụ build Gradle.
│   └── wrapper/  # Gradle Wrapper để build mà không cài Gradle riêng.
│       ├── gradle-wrapper.jar  # Bootstrap Gradle Wrapper; cần giữ để build, đây không phải JAR backend đã biên dịch.
│       └── gradle-wrapper.properties  # URL bản phân phối Gradle, timeout và vị trí cache dùng bởi Wrapper.
├── src/  # Mã nguồn và tài nguyên của dự án.
│   └── main/  # Phần được dùng khi build/chạy ứng dụng.
│       ├── java/  # Mã nguồn Java theo package.
│       │   └── vn/  # Nhóm thư mục/package vn.
│       │       └── nova/  # Nhóm thư mục/package nova.
│       │           ├── config/  # Cấu hình Spring, CORS, filter và interceptor.
│       │           │   ├── AdminWebConfiguration.java  # Đăng ký interceptor kiểm tra quyền quản trị cho các đường dẫn /api/admin/**.
│       │           │   ├── PrivateResponseHeadersFilter.java  # Gắn header chống lưu cache cho phản hồi nhạy cảm liên quan tài khoản, phiên và quản trị.
│       │           │   └── WebConfiguration.java  # Cấu hình CORS từ biến môi trường để kiểm soát nguồn web được phép gọi API.
│       │           ├── controller/  # Nhận HTTP request và chuyển dữ liệu cho service.
│       │           │   ├── AdminController.java  # Các endpoint tạo sản phẩm, danh mục, thương hiệu và banner cho tài khoản ADMIN.
│       │           │   ├── AuthController.java  # Các HTTP endpoint đăng ký, đăng nhập, OTP, khôi phục mật khẩu, kiểm tra phiên và đăng xuất; chuyển DTO sang AuthService.
│       │           │   ├── CatalogController.java  # Các endpoint công khai cho danh mục, thương hiệu, tìm sản phẩm, chi tiết sản phẩm và banner.
│       │           │   ├── HealthController.java  # Endpoint kiểm tra tình trạng API và khả năng truy vấn CSDL.
│       │           │   ├── HomeController.java  # Các endpoint dữ liệu Trang chủ, sản phẩm nổi bật, bán chạy và hàng mới về.
│       │           │   ├── MediaUploadController.java  # Nhận multipart ảnh đại diện hoặc ảnh sản phẩm; chuyển việc kiểm tra, tải Cloudinary và lưu URL cho MediaUploadService.
│       │           │   ├── SearchHistoryController.java  # Các endpoint xem, lưu và xóa lịch sử tìm kiếm của người dùng đã đăng nhập.
│       │           │   └── UserController.java  # Các endpoint hồ sơ, cập nhật hồ sơ, đổi mật khẩu và đổi email; xác định người dùng qua Bearer token.
│       │           ├── dto/  # Cấu trúc request/response JSON và validation.
│       │           │   ├── admin/  # DTO đầu vào quản trị.
│       │           │   │   └── AdminRequests.java  # Tập DTO record đầu vào quản trị; annotation Jakarta Validation mô tả các ràng buộc của từng trường.
│       │           │   ├── auth/  # DTO xác thực/OTP/phiên.
│       │           │   │   ├── AuthResponse.java  # DTO trả token đăng nhập, loại Bearer, thời hạn, thời điểm hết hạn và hồ sơ người dùng.
│       │           │   │   ├── ChallengeResponse.java  # DTO trả thông tin challenge OTP và thông báo; không chứa mã OTP thuần.
│       │           │   │   ├── EmailRequest.java  # DTO đầu vào email, dùng cho yêu cầu gửi/gửi lại OTP.
│       │           │   │   ├── LoginRequest.java  # DTO đầu vào email và mật khẩu khi đăng nhập; validation chạy trước service.
│       │           │   │   ├── RegisterRequest.java  # DTO đầu vào đăng ký gồm email, mật khẩu, tên và điện thoại cùng các ràng buộc hợp lệ.
│       │           │   │   ├── ResetPasswordRequest.java  # DTO đầu vào đặt mật khẩu mới bằng challengeId và mã OTP.
│       │           │   │   ├── SessionResponse.java  # DTO trả tình trạng phiên đã xác thực và hồ sơ hiện tại.
│       │           │   │   └── VerifyCodeRequest.java  # DTO đầu vào challengeId và mã OTP để xác minh đúng yêu cầu.
│       │           │   ├── catalog/  # Ảnh mẫu danh mục và banner của backend.
│       │           │   │   └── PageResponse.java  # DTO phân trang gồm items, total, page, size, totalPages và hasNext, giúp FE đọc danh sách nhất quán.
│       │           │   ├── user/  # DTO hồ sơ tài khoản.
│       │           │   │   ├── ChangeEmailRequest.java  # DTO đầu vào email mới và mật khẩu xác nhận khi bắt đầu đổi email.
│       │           │   │   ├── ChangePasswordRequest.java  # DTO đầu vào mật khẩu hiện tại và mật khẩu mới để thay mật khẩu.
│       │           │   │   ├── UpdateProfileRequest.java  # DTO đầu vào tên, điện thoại, ngày sinh, giới tính và URL avatar tùy chọn khi cập nhật hồ sơ.
│       │           │   │   └── UserProfileResponse.java  # DTO hồ sơ công khai trả về FE; không đưa hash mật khẩu hay thông tin xác thực server ra ngoài.
│       │           │   └── MessageResponse.java  # DTO phản hồi ngắn chỉ chứa thông báo cho các thao tác không cần trả đối tượng dữ liệu.
│       │           ├── exception/  # Lỗi nghiệp vụ và phản hồi HTTP.
│       │           │   ├── ApiException.java  # Lỗi nghiệp vụ mang HTTP status, mã lỗi và thông báo có thể trả về cho client.
│       │           │   └── ApiExceptionHandler.java  # Chuyển lỗi nghiệp vụ, validation, xung đột dữ liệu, tệp quá lớn và JDBC thành phản hồi HTTP JSON thống nhất.
│       │           ├── model/  # Dữ liệu nội bộ và bộ lọc đã chuẩn hóa.
│       │           │   ├── AccountRecord.java  # Dữ liệu tài khoản đọc từ SQL phục vụ xác thực, gồm trạng thái, vai trò, hash mật khẩu và email đã xác minh.
│       │           │   ├── CatalogFilter.java  # Record bộ lọc sản phẩm: chuẩn hóa tham số HTTP, alias danh mục/sắp xếp và kiểm tra giá, cờ, đánh giá, phân trang.
│       │           │   ├── SessionToken.java  # Dữ liệu token phiên vừa cấp kèm thời điểm hết hạn.
│       │           │   ├── VerificationChallenge.java  # Dữ liệu challenge OTP: chủ sở hữu, đích gửi, mục đích, hạn dùng và trạng thái kiểm tra.
│       │           │   └── VerificationPurpose.java  # Liệt kê các mục đích xác minh email/khôi phục mật khẩu/đổi email và mã lưu tương ứng trong CSDL.
│       │           ├── repository/  # Đọc/ghi CSDL bằng JDBC/SQL có tham số.
│       │           │   ├── AdminRepository.java  # Truy vấn và cập nhật dữ liệu quản trị; khóa sản phẩm/cây danh mục, lưu ảnh, SKU, banner và trạng thái tài khoản.
│       │           │   ├── CatalogRepository.java  # Các truy vấn JDBC đọc danh mục, thương hiệu, sản phẩm, biến thể, ảnh, thông số và banner trong schema shop.
│       │           │   ├── HealthRepository.java  # Chạy truy vấn kiểm tra kết nối JDBC đến CSDL.
│       │           │   ├── ProductSearchSql.java  # Dựng CTE, tham số và thứ tự SQL tìm sản phẩm; các tiêu chí giá/RAM/bộ nhớ/tồn kho cùng áp dụng trên tập biến thể hợp lệ.
│       │           │   ├── SessionRepository.java  # Lưu digest token phiên, tìm người dùng của phiên còn hạn và thu hồi một hoặc mọi phiên.
│       │           │   ├── UserRepository.java  # Các truy vấn JDBC cho tài khoản và hồ sơ: tạo, khóa hàng để cập nhật, đổi email/mật khẩu và lưu URL avatar.
│       │           │   └── VerificationRepository.java  # Lưu challenge OTP, khóa hàng khi xác minh, tăng số lần thử, tiêu thụ hoặc vô hiệu hóa challenge.
│       │           ├── security/  # Mật khẩu, token, OTP, giới hạn tần suất và phân quyền.
│       │           │   ├── AdminAccessInterceptor.java  # Đọc Bearer token, xác thực phiên và quyền ADMIN trước khi cho endpoint quản trị chạy.
│       │           │   ├── PasswordHasher.java  # Băm mật khẩu bằng PBKDF2-HMAC-SHA256 với salt riêng; xác minh hash đã lưu và giữ tương thích mật khẩu cũ.
│       │           │   ├── PasswordRules.java  # Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu.
│       │           │   ├── RateLimiter.java  # Giới hạn số yêu cầu theo khóa trong cửa sổ thời gian bằng bộ nhớ của tiến trình backend.
│       │           │   └── TokenFactory.java  # Sinh token ngẫu nhiên, OTP và digest/HMAC; CSDL giữ giá trị dẫn xuất thay cho token phiên thuần.
│       │           ├── service/  # Xử lý nghiệp vụ, giao dịch và dịch vụ ngoài.
│       │           │   ├── AdminService.java  # Nghiệp vụ quản trị sản phẩm/danh mục/thương hiệu/banner/người dùng; kiểm tra dữ liệu trước khi cập nhật repository.
│       │           │   ├── AuthService.java  # Điều phối đăng ký/đăng nhập; kiểm tra mật khẩu, trạng thái tài khoản, giới hạn tần suất và giao dịch tạo phiên/OTP.
│       │           │   ├── CatalogService.java  # Chuẩn hóa bộ lọc, gọi CatalogRepository và ghép dữ liệu chi tiết gồm biến thể, ảnh, thông số.
│       │           │   ├── CloudinaryImageStore.java  # Gửi ảnh từ BE tới Cloudinary bằng HTTPS và thông tin xác thực phía server; chỉ chấp nhận URL tài nguyên đúng cloud/publicId.
│       │           │   ├── EmailService.java  # Gửi email OTP qua JavaMailSender; chuyển lỗi SMTP thành lỗi API phù hợp.
│       │           │   ├── HealthService.java  # Tổng hợp tình trạng ứng dụng và kết quả kiểm tra kết nối cơ sở dữ liệu.
│       │           │   ├── HomeService.java  # Ghép dữ liệu Trang chủ và tạo các trang sản phẩm theo tiêu chí nổi bật, bán chạy hoặc mới về.
│       │           │   ├── ImageUploadValidator.java  # Kiểm tra nội dung thật của ảnh, loại định dạng, kích thước, giới hạn dung lượng và tạo SHA-256 để nhận diện ảnh trùng.
│       │           │   ├── MediaUploadService.java  # Xác thực người tải ảnh, kiểm tra giới hạn ảnh, chống trùng bằng SHA-256, tải Cloudinary rồi ghi URL vào Neon trong luồng giao dịch.
│       │           │   ├── SessionService.java  # Tạo và xác thực phiên Bearer, đổi token thành userId, kiểm tra hạn dùng và thu hồi phiên.
│       │           │   ├── UserService.java  # Xử lý hồ sơ, đổi mật khẩu/email, thu hồi phiên khi cần và kiểm tra URL ảnh đại diện.
│       │           │   └── VerificationService.java  # Phát và kiểm tra challenge OTP theo mục đích; quản lý hạn dùng, số lần thử, tiêu thụ mã và giao dịch xác minh.
│       │           ├── util/  # Hàm hỗ trợ chuẩn hóa dữ liệu và JDBC.
│       │           │   ├── JdbcExecutor.java  # Lớp bọc JdbcTemplate để truy vấn danh sách/một dòng/giá trị số hoặc cập nhật với tham số ràng buộc.
│       │           │   └── RequestRules.java  # Chuẩn hóa email/số điện thoại và chuyển chuỗi trống thành null trước khi xử lý nghiệp vụ.
│       │           └── NovaApplication.java  # Điểm vào Spring Boot; khởi tạo application context và quét component trong package vn.nova.
│       └── resources/  # Cấu hình ứng dụng và tài nguyên backend.
│           ├── static/  # Tài nguyên được backend phục vụ qua HTTP.
│           │   └── media/  # Các ảnh tĩnh trong API media.
│           │       └── catalog/  # Ảnh mẫu danh mục và banner của backend.
│           │           ├── banners/  # Ảnh banner.
│           │           │   ├── nova-galaxy-20261105.jpg  # Ảnh tĩnh phục vụ endpoint media của backend: nova-galaxy-20261105.
│           │           │   ├── nova-iphone-20261105.jpg  # Ảnh tĩnh phục vụ endpoint media của backend: nova-iphone-20261105.
│           │           │   └── nova-sony-20261105.jpg  # Ảnh tĩnh phục vụ endpoint media của backend: nova-sony-20261105.
│           │           └── categories/  # Ảnh danh mục.
│           │               ├── accessory-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: accessory-v1.
│           │               ├── android-v1.jpg  # Ảnh tĩnh phục vụ endpoint media của backend: android-v1.
│           │               ├── audio-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: audio-v1.
│           │               ├── keyboard-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: keyboard-v1.
│           │               ├── laptop-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: laptop-v1.
│           │               ├── phone-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: phone-v1.
│           │               ├── tablet-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: tablet-v1.
│           │               └── watch-v1.png  # Ảnh tĩnh phục vụ endpoint media của backend: watch-v1.
│           └── application.properties  # Cấu hình Spring Boot: HTTP, JDBC schema shop, SMTP, CORS, OTP và Cloudinary; bí mật đọc từ biến môi trường.
├── .env.cloudinary.example  # Mẫu biến môi trường để đọc cấu hình backend; giá trị mật khẩu/khóa là placeholder, không phải cấu hình thật.
├── .env.example  # Mẫu biến môi trường để đọc cấu hình backend; giá trị mật khẩu/khóa là placeholder, không phải cấu hình thật.
├── .gitignore  # Quy tắc bỏ qua file build, cache, cấu hình máy, Python và bí mật khi đưa mã nguồn lên Git.
├── build.gradle  # Cấu hình module, plugin, kho thư viện hoặc quy tắc build Gradle cho NOVA_GK.
├── CAU-TRUC-BE.md  # Cây thư mục BE đầy đủ; mỗi file có chú thích chức năng ngay trên dòng tương ứng.
├── gradlew  # Script Gradle Wrapper do Gradle sinh; khởi động đúng phiên bản Gradle trên Unix/macOS/Linux.
├── gradlew.bat  # Script Gradle Wrapper do Gradle sinh; khởi động đúng phiên bản Gradle trên Windows.
├── README.md  # Hướng dẫn mở/đọc gói BE độc lập và các liên kết tài liệu của gói.
└── settings.gradle  # Cấu hình module, plugin, kho thư viện hoặc quy tắc build Gradle cho NOVA_GK.
```

[Bảng chức năng từng file](docs/CHUC-NANG-TUNG-FILE.md) · [Tra cứu phương thức](docs/TRA-CUU-HAM.md) · [Giải thích code](docs/GIAI-THICH-CODE.md)
