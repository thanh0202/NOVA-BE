# Chức năng từng file BE

110 file trong gói NOVA_BE_GITHUB. Các đường dẫn chỉ trỏ tới file của gói này.

| File | Chức năng |
| --- | --- |
| [.env.cloudinary.example](../.env.cloudinary.example) | Mẫu biến môi trường để đọc cấu hình backend; giá trị mật khẩu/khóa là placeholder, không phải cấu hình thật. |
| [.env.example](../.env.example) | Mẫu biến môi trường để đọc cấu hình backend; giá trị mật khẩu/khóa là placeholder, không phải cấu hình thật. |
| [.gitignore](../.gitignore) | Quy tắc bỏ qua file build, cache, cấu hình máy, Python và bí mật khi đưa mã nguồn lên Git. |
| [build.gradle](../build.gradle) | Cấu hình module, plugin, kho thư viện hoặc quy tắc build Gradle cho NOVA_GK. |
| [CAU-TRUC-BE.md](../CAU-TRUC-BE.md) | Cây thư mục BE đầy đủ; mỗi file có chú thích chức năng ngay trên dòng tương ứng. |
| [database/017_home_featured.sql](../database/017_home_featured.sql) | Migration chọn một nhóm sản phẩm hiện có làm nổi bật; giữ dữ liệu giá, ảnh và tồn kho, không tạo lại toàn bộ CSDL. |
| [docs/CHUC-NANG-TUNG-FILE.md](../docs/CHUC-NANG-TUNG-FILE.md) | Bảng chức năng toàn bộ file trong gói BE. |
| [docs/csdl/ERD-DON-HANG.mmd](../docs/csdl/ERD-DON-HANG.mmd) | Mã Mermaid của sơ đồ Giỏ hàng, đơn hàng và giao nhận, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-DON-HANG.svg](../docs/csdl/ERD-DON-HANG.svg) | Sơ đồ ERD vector Giỏ hàng, đơn hàng và giao nhận; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-HE-THONG-HO-TRO.mmd](../docs/csdl/ERD-HE-THONG-HO-TRO.mmd) | Mã Mermaid của sơ đồ Thông báo, hỗ trợ và vận hành, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-HE-THONG-HO-TRO.svg](../docs/csdl/ERD-HE-THONG-HO-TRO.svg) | Sơ đồ ERD vector Thông báo, hỗ trợ và vận hành; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-KHO-HANG.mmd](../docs/csdl/ERD-KHO-HANG.mmd) | Mã Mermaid của sơ đồ Kho và nhập hàng, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-KHO-HANG.svg](../docs/csdl/ERD-KHO-HANG.svg) | Sơ đồ ERD vector Kho và nhập hàng; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-KHUYEN-MAI.mmd](../docs/csdl/ERD-KHUYEN-MAI.mmd) | Mã Mermaid của sơ đồ Voucher và Flash Sale, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-KHUYEN-MAI.svg](../docs/csdl/ERD-KHUYEN-MAI.svg) | Sơ đồ ERD vector Voucher và Flash Sale; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-SAN-PHAM.mmd](../docs/csdl/ERD-SAN-PHAM.mmd) | Mã Mermaid của sơ đồ Danh mục, sản phẩm và đánh giá, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-SAN-PHAM.svg](../docs/csdl/ERD-SAN-PHAM.svg) | Sơ đồ ERD vector Danh mục, sản phẩm và đánh giá; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-TAI-KHOAN.mmd](../docs/csdl/ERD-TAI-KHOAN.mmd) | Mã Mermaid của sơ đồ Tài khoản và xác thực, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-TAI-KHOAN.svg](../docs/csdl/ERD-TAI-KHOAN.svg) | Sơ đồ ERD vector Tài khoản và xác thực; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-THANH-TOAN-DOI-TRA.mmd](../docs/csdl/ERD-THANH-TOAN-DOI-TRA.mmd) | Mã Mermaid của sơ đồ Thanh toán, đổi trả và hoàn tiền, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-THANH-TOAN-DOI-TRA.svg](../docs/csdl/ERD-THANH-TOAN-DOI-TRA.svg) | Sơ đồ ERD vector Thanh toán, đổi trả và hoàn tiền; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-THANH-VIEN.mmd](../docs/csdl/ERD-THANH-VIEN.mmd) | Mã Mermaid của sơ đồ Thành viên và điểm thưởng, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-THANH-VIEN.svg](../docs/csdl/ERD-THANH-VIEN.svg) | Sơ đồ ERD vector Thành viên và điểm thưởng; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/ERD-TOAN-BO.mmd](../docs/csdl/ERD-TOAN-BO.mmd) | Mã Mermaid của sơ đồ Toàn bộ 66 bảng, có đủ cột và các khóa ngoại thuộc phạm vi. |
| [docs/csdl/ERD-TOAN-BO.svg](../docs/csdl/ERD-TOAN-BO.svg) | Sơ đồ ERD vector Toàn bộ 66 bảng; mở trực tiếp hoặc xem trong trang HTML. |
| [docs/csdl/SCHEMA-NEON.json](../docs/csdl/SCHEMA-NEON.json) | Metadata chỉ đọc từ catalog Neon, dùng làm nguồn sinh tài liệu/ERD; không chứa hàng dữ liệu hay thông tin đăng nhập. |
| [docs/csdl/SO-DO-CSDL.html](../docs/csdl/SO-DO-CSDL.html) | Trình xem ERD độc lập/offline: chọn nhóm, kéo, tìm bảng và phóng to/thu nhỏ. |
| [docs/csdl/SO-DO-CSDL.md](../docs/csdl/SO-DO-CSDL.md) | Trang tổng hợp sơ đồ toàn bộ CSDL và từng nhóm nghiệp vụ, kèm SVG và nguồn Mermaid. |
| [docs/csdl/TU-DIEN-DU-LIEU.md](../docs/csdl/TU-DIEN-DU-LIEU.md) | Từ điển đủ 495 cột, PK/FK/UNIQUE/CHECK, 212 index, 15 trigger, 12 view và 17 hàm/thủ tục. |
| [docs/GIAI-THICH-CODE.md](../docs/GIAI-THICH-CODE.md) | Giải thích luồng xử lý và cách đọc mã BE. |
| [docs/GIAI-THICH-CSDL.md](../docs/GIAI-THICH-CSDL.md) | Giải thích CSDL Neon hiện có: 66 bảng, các nhóm nghiệp vụ, quan hệ, quy tắc lưu trữ và sơ đồ ERD. |
| [docs/TRA-CUU-HAM.md](../docs/TRA-CUU-HAM.md) | Tra cứu phương thức Java và liên kết tới đúng dòng mã nguồn BE. |
| [gradle/wrapper/gradle-wrapper.jar](../gradle/wrapper/gradle-wrapper.jar) | Bootstrap Gradle Wrapper; cần giữ để build, đây không phải JAR backend đã biên dịch. |
| [gradle/wrapper/gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties) | URL bản phân phối Gradle, timeout và vị trí cache dùng bởi Wrapper. |
| [gradlew](../gradlew) | Script Gradle Wrapper do Gradle sinh; khởi động đúng phiên bản Gradle trên Unix/macOS/Linux. |
| [gradlew.bat](../gradlew.bat) | Script Gradle Wrapper do Gradle sinh; khởi động đúng phiên bản Gradle trên Windows. |
| [README.md](../README.md) | Hướng dẫn mở/đọc gói BE độc lập và các liên kết tài liệu của gói. |
| [settings.gradle](../settings.gradle) | Cấu hình module, plugin, kho thư viện hoặc quy tắc build Gradle cho NOVA_GK. |
| [src/main/java/vn/nova/config/AdminWebConfiguration.java](../src/main/java/vn/nova/config/AdminWebConfiguration.java) | Đăng ký interceptor kiểm tra quyền quản trị cho các đường dẫn /api/admin/**. |
| [src/main/java/vn/nova/config/PrivateResponseHeadersFilter.java](../src/main/java/vn/nova/config/PrivateResponseHeadersFilter.java) | Gắn header chống lưu cache cho phản hồi nhạy cảm liên quan tài khoản, phiên và quản trị. |
| [src/main/java/vn/nova/config/WebConfiguration.java](../src/main/java/vn/nova/config/WebConfiguration.java) | Cấu hình CORS từ biến môi trường để kiểm soát nguồn web được phép gọi API. |
| [src/main/java/vn/nova/controller/AdminController.java](../src/main/java/vn/nova/controller/AdminController.java) | Các endpoint tạo sản phẩm, danh mục, thương hiệu và banner cho tài khoản ADMIN. |
| [src/main/java/vn/nova/controller/AuthController.java](../src/main/java/vn/nova/controller/AuthController.java) | Các HTTP endpoint đăng ký, đăng nhập, OTP, khôi phục mật khẩu, kiểm tra phiên và đăng xuất; chuyển DTO sang AuthService. |
| [src/main/java/vn/nova/controller/CatalogController.java](../src/main/java/vn/nova/controller/CatalogController.java) | Các endpoint công khai cho danh mục, thương hiệu, tìm sản phẩm, chi tiết sản phẩm và banner. |
| [src/main/java/vn/nova/controller/HealthController.java](../src/main/java/vn/nova/controller/HealthController.java) | Endpoint kiểm tra tình trạng API và khả năng truy vấn CSDL. |
| [src/main/java/vn/nova/controller/HomeController.java](../src/main/java/vn/nova/controller/HomeController.java) | Các endpoint dữ liệu Trang chủ, sản phẩm nổi bật, bán chạy và hàng mới về. |
| [src/main/java/vn/nova/controller/MediaUploadController.java](../src/main/java/vn/nova/controller/MediaUploadController.java) | Nhận multipart ảnh đại diện hoặc ảnh sản phẩm; chuyển việc kiểm tra, tải Cloudinary và lưu URL cho MediaUploadService. |
| [src/main/java/vn/nova/controller/SearchHistoryController.java](../src/main/java/vn/nova/controller/SearchHistoryController.java) | Các endpoint xem, lưu và xóa lịch sử tìm kiếm của người dùng đã đăng nhập. |
| [src/main/java/vn/nova/controller/UserController.java](../src/main/java/vn/nova/controller/UserController.java) | Các endpoint hồ sơ, cập nhật hồ sơ, đổi mật khẩu và đổi email; xác định người dùng qua Bearer token. |
| [src/main/java/vn/nova/dto/admin/AdminRequests.java](../src/main/java/vn/nova/dto/admin/AdminRequests.java) | Tập DTO record đầu vào quản trị; annotation Jakarta Validation mô tả các ràng buộc của từng trường. |
| [src/main/java/vn/nova/dto/auth/AuthResponse.java](../src/main/java/vn/nova/dto/auth/AuthResponse.java) | DTO trả token đăng nhập, loại Bearer, thời hạn, thời điểm hết hạn và hồ sơ người dùng. |
| [src/main/java/vn/nova/dto/auth/ChallengeResponse.java](../src/main/java/vn/nova/dto/auth/ChallengeResponse.java) | DTO trả thông tin challenge OTP và thông báo; không chứa mã OTP thuần. |
| [src/main/java/vn/nova/dto/auth/EmailRequest.java](../src/main/java/vn/nova/dto/auth/EmailRequest.java) | DTO đầu vào email, dùng cho yêu cầu gửi/gửi lại OTP. |
| [src/main/java/vn/nova/dto/auth/LoginRequest.java](../src/main/java/vn/nova/dto/auth/LoginRequest.java) | DTO đầu vào email và mật khẩu khi đăng nhập; validation chạy trước service. |
| [src/main/java/vn/nova/dto/auth/RegisterRequest.java](../src/main/java/vn/nova/dto/auth/RegisterRequest.java) | DTO đầu vào đăng ký gồm email, mật khẩu, tên và điện thoại cùng các ràng buộc hợp lệ. |
| [src/main/java/vn/nova/dto/auth/ResetPasswordRequest.java](../src/main/java/vn/nova/dto/auth/ResetPasswordRequest.java) | DTO đầu vào đặt mật khẩu mới bằng challengeId và mã OTP. |
| [src/main/java/vn/nova/dto/auth/SessionResponse.java](../src/main/java/vn/nova/dto/auth/SessionResponse.java) | DTO trả tình trạng phiên đã xác thực và hồ sơ hiện tại. |
| [src/main/java/vn/nova/dto/auth/VerifyCodeRequest.java](../src/main/java/vn/nova/dto/auth/VerifyCodeRequest.java) | DTO đầu vào challengeId và mã OTP để xác minh đúng yêu cầu. |
| [src/main/java/vn/nova/dto/catalog/PageResponse.java](../src/main/java/vn/nova/dto/catalog/PageResponse.java) | DTO phân trang gồm items, total, page, size, totalPages và hasNext, giúp FE đọc danh sách nhất quán. |
| [src/main/java/vn/nova/dto/MessageResponse.java](../src/main/java/vn/nova/dto/MessageResponse.java) | DTO phản hồi ngắn chỉ chứa thông báo cho các thao tác không cần trả đối tượng dữ liệu. |
| [src/main/java/vn/nova/dto/user/ChangeEmailRequest.java](../src/main/java/vn/nova/dto/user/ChangeEmailRequest.java) | DTO đầu vào email mới và mật khẩu xác nhận khi bắt đầu đổi email. |
| [src/main/java/vn/nova/dto/user/ChangePasswordRequest.java](../src/main/java/vn/nova/dto/user/ChangePasswordRequest.java) | DTO đầu vào mật khẩu hiện tại và mật khẩu mới để thay mật khẩu. |
| [src/main/java/vn/nova/dto/user/UpdateProfileRequest.java](../src/main/java/vn/nova/dto/user/UpdateProfileRequest.java) | DTO đầu vào tên, điện thoại, ngày sinh, giới tính và URL avatar tùy chọn khi cập nhật hồ sơ. |
| [src/main/java/vn/nova/dto/user/UserProfileResponse.java](../src/main/java/vn/nova/dto/user/UserProfileResponse.java) | DTO hồ sơ công khai trả về FE; không đưa hash mật khẩu hay thông tin xác thực server ra ngoài. |
| [src/main/java/vn/nova/exception/ApiException.java](../src/main/java/vn/nova/exception/ApiException.java) | Lỗi nghiệp vụ mang HTTP status, mã lỗi và thông báo có thể trả về cho client. |
| [src/main/java/vn/nova/exception/ApiExceptionHandler.java](../src/main/java/vn/nova/exception/ApiExceptionHandler.java) | Chuyển lỗi nghiệp vụ, validation, xung đột dữ liệu, tệp quá lớn và JDBC thành phản hồi HTTP JSON thống nhất. |
| [src/main/java/vn/nova/model/AccountRecord.java](../src/main/java/vn/nova/model/AccountRecord.java) | Dữ liệu tài khoản đọc từ SQL phục vụ xác thực, gồm trạng thái, vai trò, hash mật khẩu và email đã xác minh. |
| [src/main/java/vn/nova/model/CatalogFilter.java](../src/main/java/vn/nova/model/CatalogFilter.java) | Record bộ lọc sản phẩm: chuẩn hóa tham số HTTP, alias danh mục/sắp xếp và kiểm tra giá, cờ, đánh giá, phân trang. |
| [src/main/java/vn/nova/model/SessionToken.java](../src/main/java/vn/nova/model/SessionToken.java) | Dữ liệu token phiên vừa cấp kèm thời điểm hết hạn. |
| [src/main/java/vn/nova/model/VerificationChallenge.java](../src/main/java/vn/nova/model/VerificationChallenge.java) | Dữ liệu challenge OTP: chủ sở hữu, đích gửi, mục đích, hạn dùng và trạng thái kiểm tra. |
| [src/main/java/vn/nova/model/VerificationPurpose.java](../src/main/java/vn/nova/model/VerificationPurpose.java) | Liệt kê các mục đích xác minh email/khôi phục mật khẩu/đổi email và mã lưu tương ứng trong CSDL. |
| [src/main/java/vn/nova/NovaApplication.java](../src/main/java/vn/nova/NovaApplication.java) | Điểm vào Spring Boot; khởi tạo application context và quét component trong package vn.nova. |
| [src/main/java/vn/nova/repository/AdminRepository.java](../src/main/java/vn/nova/repository/AdminRepository.java) | Truy vấn và cập nhật dữ liệu quản trị; khóa sản phẩm/cây danh mục, lưu ảnh, SKU, banner và trạng thái tài khoản. |
| [src/main/java/vn/nova/repository/CatalogRepository.java](../src/main/java/vn/nova/repository/CatalogRepository.java) | Các truy vấn JDBC đọc danh mục, thương hiệu, sản phẩm, biến thể, ảnh, thông số và banner trong schema shop. |
| [src/main/java/vn/nova/repository/HealthRepository.java](../src/main/java/vn/nova/repository/HealthRepository.java) | Chạy truy vấn kiểm tra kết nối JDBC đến CSDL. |
| [src/main/java/vn/nova/repository/ProductSearchSql.java](../src/main/java/vn/nova/repository/ProductSearchSql.java) | Dựng CTE, tham số và thứ tự SQL tìm sản phẩm; các tiêu chí giá/RAM/bộ nhớ/tồn kho cùng áp dụng trên tập biến thể hợp lệ. |
| [src/main/java/vn/nova/repository/SessionRepository.java](../src/main/java/vn/nova/repository/SessionRepository.java) | Lưu digest token phiên, tìm người dùng của phiên còn hạn và thu hồi một hoặc mọi phiên. |
| [src/main/java/vn/nova/repository/UserRepository.java](../src/main/java/vn/nova/repository/UserRepository.java) | Các truy vấn JDBC cho tài khoản và hồ sơ: tạo, khóa hàng để cập nhật, đổi email/mật khẩu và lưu URL avatar. |
| [src/main/java/vn/nova/repository/VerificationRepository.java](../src/main/java/vn/nova/repository/VerificationRepository.java) | Lưu challenge OTP, khóa hàng khi xác minh, tăng số lần thử, tiêu thụ hoặc vô hiệu hóa challenge. |
| [src/main/java/vn/nova/security/AdminAccessInterceptor.java](../src/main/java/vn/nova/security/AdminAccessInterceptor.java) | Đọc Bearer token, xác thực phiên và quyền ADMIN trước khi cho endpoint quản trị chạy. |
| [src/main/java/vn/nova/security/PasswordHasher.java](../src/main/java/vn/nova/security/PasswordHasher.java) | Băm mật khẩu bằng PBKDF2-HMAC-SHA256 với salt riêng; xác minh hash đã lưu và giữ tương thích mật khẩu cũ. |
| [src/main/java/vn/nova/security/PasswordRules.java](../src/main/java/vn/nova/security/PasswordRules.java) | Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu. |
| [src/main/java/vn/nova/security/RateLimiter.java](../src/main/java/vn/nova/security/RateLimiter.java) | Giới hạn số yêu cầu theo khóa trong cửa sổ thời gian bằng bộ nhớ của tiến trình backend. |
| [src/main/java/vn/nova/security/TokenFactory.java](../src/main/java/vn/nova/security/TokenFactory.java) | Sinh token ngẫu nhiên, OTP và digest/HMAC; CSDL giữ giá trị dẫn xuất thay cho token phiên thuần. |
| [src/main/java/vn/nova/service/AdminService.java](../src/main/java/vn/nova/service/AdminService.java) | Nghiệp vụ quản trị sản phẩm/danh mục/thương hiệu/banner/người dùng; kiểm tra dữ liệu trước khi cập nhật repository. |
| [src/main/java/vn/nova/service/AuthService.java](../src/main/java/vn/nova/service/AuthService.java) | Điều phối đăng ký/đăng nhập; kiểm tra mật khẩu, trạng thái tài khoản, giới hạn tần suất và giao dịch tạo phiên/OTP. |
| [src/main/java/vn/nova/service/CatalogService.java](../src/main/java/vn/nova/service/CatalogService.java) | Chuẩn hóa bộ lọc, gọi CatalogRepository và ghép dữ liệu chi tiết gồm biến thể, ảnh, thông số. |
| [src/main/java/vn/nova/service/CloudinaryImageStore.java](../src/main/java/vn/nova/service/CloudinaryImageStore.java) | Gửi ảnh từ BE tới Cloudinary bằng HTTPS và thông tin xác thực phía server; chỉ chấp nhận URL tài nguyên đúng cloud/publicId. |
| [src/main/java/vn/nova/service/EmailService.java](../src/main/java/vn/nova/service/EmailService.java) | Gửi email OTP qua JavaMailSender; chuyển lỗi SMTP thành lỗi API phù hợp. |
| [src/main/java/vn/nova/service/HealthService.java](../src/main/java/vn/nova/service/HealthService.java) | Tổng hợp tình trạng ứng dụng và kết quả kiểm tra kết nối cơ sở dữ liệu. |
| [src/main/java/vn/nova/service/HomeService.java](../src/main/java/vn/nova/service/HomeService.java) | Ghép dữ liệu Trang chủ và tạo các trang sản phẩm theo tiêu chí nổi bật, bán chạy hoặc mới về. |
| [src/main/java/vn/nova/service/ImageUploadValidator.java](../src/main/java/vn/nova/service/ImageUploadValidator.java) | Kiểm tra nội dung thật của ảnh, loại định dạng, kích thước, giới hạn dung lượng và tạo SHA-256 để nhận diện ảnh trùng. |
| [src/main/java/vn/nova/service/MediaUploadService.java](../src/main/java/vn/nova/service/MediaUploadService.java) | Xác thực người tải ảnh, kiểm tra giới hạn ảnh, chống trùng bằng SHA-256, tải Cloudinary rồi ghi URL vào Neon trong luồng giao dịch. |
| [src/main/java/vn/nova/service/SessionService.java](../src/main/java/vn/nova/service/SessionService.java) | Tạo và xác thực phiên Bearer, đổi token thành userId, kiểm tra hạn dùng và thu hồi phiên. |
| [src/main/java/vn/nova/service/UserService.java](../src/main/java/vn/nova/service/UserService.java) | Xử lý hồ sơ, đổi mật khẩu/email, thu hồi phiên khi cần và kiểm tra URL ảnh đại diện. |
| [src/main/java/vn/nova/service/VerificationService.java](../src/main/java/vn/nova/service/VerificationService.java) | Phát và kiểm tra challenge OTP theo mục đích; quản lý hạn dùng, số lần thử, tiêu thụ mã và giao dịch xác minh. |
| [src/main/java/vn/nova/util/JdbcExecutor.java](../src/main/java/vn/nova/util/JdbcExecutor.java) | Lớp bọc JdbcTemplate để truy vấn danh sách/một dòng/giá trị số hoặc cập nhật với tham số ràng buộc. |
| [src/main/java/vn/nova/util/RequestRules.java](../src/main/java/vn/nova/util/RequestRules.java) | Chuẩn hóa email/số điện thoại và chuyển chuỗi trống thành null trước khi xử lý nghiệp vụ. |
| [src/main/resources/application.properties](../src/main/resources/application.properties) | Cấu hình Spring Boot: HTTP, JDBC schema shop, SMTP, CORS, OTP và Cloudinary; bí mật đọc từ biến môi trường. |
| [src/main/resources/static/media/catalog/banners/nova-galaxy-20261105.jpg](../src/main/resources/static/media/catalog/banners/nova-galaxy-20261105.jpg) | Ảnh tĩnh phục vụ endpoint media của backend: nova-galaxy-20261105. |
| [src/main/resources/static/media/catalog/banners/nova-iphone-20261105.jpg](../src/main/resources/static/media/catalog/banners/nova-iphone-20261105.jpg) | Ảnh tĩnh phục vụ endpoint media của backend: nova-iphone-20261105. |
| [src/main/resources/static/media/catalog/banners/nova-sony-20261105.jpg](../src/main/resources/static/media/catalog/banners/nova-sony-20261105.jpg) | Ảnh tĩnh phục vụ endpoint media của backend: nova-sony-20261105. |
| [src/main/resources/static/media/catalog/categories/accessory-v1.png](../src/main/resources/static/media/catalog/categories/accessory-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: accessory-v1. |
| [src/main/resources/static/media/catalog/categories/android-v1.jpg](../src/main/resources/static/media/catalog/categories/android-v1.jpg) | Ảnh tĩnh phục vụ endpoint media của backend: android-v1. |
| [src/main/resources/static/media/catalog/categories/audio-v1.png](../src/main/resources/static/media/catalog/categories/audio-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: audio-v1. |
| [src/main/resources/static/media/catalog/categories/keyboard-v1.png](../src/main/resources/static/media/catalog/categories/keyboard-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: keyboard-v1. |
| [src/main/resources/static/media/catalog/categories/laptop-v1.png](../src/main/resources/static/media/catalog/categories/laptop-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: laptop-v1. |
| [src/main/resources/static/media/catalog/categories/phone-v1.png](../src/main/resources/static/media/catalog/categories/phone-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: phone-v1. |
| [src/main/resources/static/media/catalog/categories/tablet-v1.png](../src/main/resources/static/media/catalog/categories/tablet-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: tablet-v1. |
| [src/main/resources/static/media/catalog/categories/watch-v1.png](../src/main/resources/static/media/catalog/categories/watch-v1.png) | Ảnh tĩnh phục vụ endpoint media của backend: watch-v1. |
