# Tra cứu phương thức BE

Các liên kết dưới đây chỉ trỏ tới mã nguồn bên trong gói NOVA_BE_GITHUB.

## NovaApplication

[Mở file](../src/main/java/vn/nova/NovaApplication.java) — Điểm vào Spring Boot; khởi tạo application context và quét component trong package vn.nova.

| Phương thức | Chức năng |
| --- | --- |
| [main()](../src/main/java/vn/nova/NovaApplication.java#L14) | Khởi động SpringApplication và toàn bộ bean backend. |

## AdminWebConfiguration

[Mở file](../src/main/java/vn/nova/config/AdminWebConfiguration.java) — Đăng ký interceptor kiểm tra quyền quản trị cho các đường dẫn /api/admin/**.

| Phương thức | Chức năng |
| --- | --- |
| [AdminWebConfiguration()](../src/main/java/vn/nova/config/AdminWebConfiguration.java#L17) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [addInterceptors()](../src/main/java/vn/nova/config/AdminWebConfiguration.java#L23) | Gắn bộ kiểm tra quyền quản trị vào các đường dẫn được cấu hình. |

## PrivateResponseHeadersFilter

[Mở file](../src/main/java/vn/nova/config/PrivateResponseHeadersFilter.java) — Gắn header chống lưu cache cho phản hồi nhạy cảm liên quan tài khoản, phiên và quản trị.

| Phương thức | Chức năng |
| --- | --- |
| [doFilterInternal()](../src/main/java/vn/nova/config/PrivateResponseHeadersFilter.java#L20) | Kiểm tra URL phản hồi và thêm header cache phù hợp trước khi tiếp tục filter chain. |

## WebConfiguration

[Mở file](../src/main/java/vn/nova/config/WebConfiguration.java) — Cấu hình CORS từ biến môi trường để kiểm soát nguồn web được phép gọi API.

| Phương thức | Chức năng |
| --- | --- |
| [WebConfiguration()](../src/main/java/vn/nova/config/WebConfiguration.java#L19) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [addCorsMappings()](../src/main/java/vn/nova/config/WebConfiguration.java#L28) | Áp dụng danh sách origin, method và header được phép qua CORS. |

## AdminController

[Mở file](../src/main/java/vn/nova/controller/AdminController.java) — Các endpoint tạo sản phẩm, danh mục, thương hiệu và banner cho tài khoản ADMIN.

| Phương thức | Chức năng |
| --- | --- |
| [AdminController()](../src/main/java/vn/nova/controller/AdminController.java#L24) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [dashboard()](../src/main/java/vn/nova/controller/AdminController.java#L27) | Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL. |
| [products()](../src/main/java/vn/nova/controller/AdminController.java#L29) | Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại. |
| [product()](../src/main/java/vn/nova/controller/AdminController.java#L35) | Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID. |
| [createProduct()](../src/main/java/vn/nova/controller/AdminController.java#L38) | Kiểm tra/tạo sản phẩm và các dữ liệu liên quan theo tầng controller/repository. |
| [updateProduct()](../src/main/java/vn/nova/controller/AdminController.java#L40) | Cập nhật các trường sản phẩm đã được kiểm tra hợp lệ. |
| [archiveProduct()](../src/main/java/vn/nova/controller/AdminController.java#L42) | Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ. |
| [categories()](../src/main/java/vn/nova/controller/AdminController.java#L45) | Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại. |
| [createCategory()](../src/main/java/vn/nova/controller/AdminController.java#L48) | Tạo danh mục sau khi kiểm tra dữ liệu đầu vào/quyền quản trị. |
| [updateCategory()](../src/main/java/vn/nova/controller/AdminController.java#L50) | Cập nhật danh mục theo ID và kiểm tra quan hệ cây. |
| [brands()](../src/main/java/vn/nova/controller/AdminController.java#L52) | Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị. |
| [createBrand()](../src/main/java/vn/nova/controller/AdminController.java#L55) | Tạo thương hiệu với các trường hợp lệ. |
| [updateBrand()](../src/main/java/vn/nova/controller/AdminController.java#L57) | Cập nhật thương hiệu hiện có theo ID. |
| [banners()](../src/main/java/vn/nova/controller/AdminController.java#L59) | Đọc các banner hợp lệ theo dữ liệu CSDL. |
| [createBanner()](../src/main/java/vn/nova/controller/AdminController.java#L62) | Tạo banner quản trị cùng URL ảnh và liên kết sản phẩm. |
| [updateBanner()](../src/main/java/vn/nova/controller/AdminController.java#L64) | Cập nhật nội dung/trạng thái banner theo ID. |
| [users()](../src/main/java/vn/nova/controller/AdminController.java#L66) | Lấy danh sách người dùng theo bộ lọc và phân trang quản trị. |
| [userStatus()](../src/main/java/vn/nova/controller/AdminController.java#L69) | Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị. |

## AuthController

[Mở file](../src/main/java/vn/nova/controller/AuthController.java) — Các HTTP endpoint đăng ký, đăng nhập, OTP, khôi phục mật khẩu, kiểm tra phiên và đăng xuất; chuyển DTO sang AuthService.

| Phương thức | Chức năng |
| --- | --- |
| [AuthController()](../src/main/java/vn/nova/controller/AuthController.java#L37) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [register()](../src/main/java/vn/nova/controller/AuthController.java#L44) | Chuẩn hóa dữ liệu đăng ký, tạo tài khoản/challenge OTP hoặc chuyển yêu cầu tới service tương ứng. |
| [login()](../src/main/java/vn/nova/controller/AuthController.java#L50) | Kiểm tra thông tin đăng nhập và cấp phản hồi phiên sau khi tài khoản đủ điều kiện. |
| [verifyEmail()](../src/main/java/vn/nova/controller/AuthController.java#L56) | Xác minh challenge OTP đăng ký rồi cập nhật trạng thái email và cấp phiên. |
| [resend()](../src/main/java/vn/nova/controller/AuthController.java#L62) | Yêu cầu phát lại challenge xác minh email theo giới hạn tần suất. |
| [forgotPassword()](../src/main/java/vn/nova/controller/AuthController.java#L68) | Bắt đầu luồng khôi phục mật khẩu bằng challenge gửi tới email. |
| [verifyResetCode()](../src/main/java/vn/nova/controller/AuthController.java#L74) | Kiểm tra OTP khôi phục trước khi cho phép chuyển sang đặt mật khẩu mới. |
| [resetPassword()](../src/main/java/vn/nova/controller/AuthController.java#L80) | Xác minh challenge, lưu hash mật khẩu mới và thu hồi các phiên cũ. |
| [currentSession()](../src/main/java/vn/nova/controller/AuthController.java#L86) | Đọc hồ sơ của người dùng từ token hợp lệ để xác minh phiên hiện tại. |
| [logout()](../src/main/java/vn/nova/controller/AuthController.java#L92) | Thu hồi phiên hiện tại và trả thông báo đăng xuất. |

## CatalogController

[Mở file](../src/main/java/vn/nova/controller/CatalogController.java) — Các endpoint công khai cho danh mục, thương hiệu, tìm sản phẩm, chi tiết sản phẩm và banner.

| Phương thức | Chức năng |
| --- | --- |
| [CatalogController()](../src/main/java/vn/nova/controller/CatalogController.java#L25) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [search()](../src/main/java/vn/nova/controller/CatalogController.java#L31) | Chuẩn hóa bộ lọc và trả trang sản phẩm phù hợp. |
| [product()](../src/main/java/vn/nova/controller/CatalogController.java#L37) | Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID. |
| [banners()](../src/main/java/vn/nova/controller/CatalogController.java#L43) | Đọc các banner hợp lệ theo dữ liệu CSDL. |
| [categories()](../src/main/java/vn/nova/controller/CatalogController.java#L49) | Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại. |
| [brands()](../src/main/java/vn/nova/controller/CatalogController.java#L55) | Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị. |

## HealthController

[Mở file](../src/main/java/vn/nova/controller/HealthController.java) — Endpoint kiểm tra tình trạng API và khả năng truy vấn CSDL.

| Phương thức | Chức năng |
| --- | --- |
| [HealthController()](../src/main/java/vn/nova/controller/HealthController.java#L19) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [check()](../src/main/java/vn/nova/controller/HealthController.java#L25) | Nhận GET health và trả kết quả HealthService. |

## HomeController

[Mở file](../src/main/java/vn/nova/controller/HomeController.java) — Các endpoint dữ liệu Trang chủ, sản phẩm nổi bật, bán chạy và hàng mới về.

| Phương thức | Chức năng |
| --- | --- |
| [HomeController()](../src/main/java/vn/nova/controller/HomeController.java#L23) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [home()](../src/main/java/vn/nova/controller/HomeController.java#L29) | Ghép các nhóm sản phẩm của Trang chủ trong một phản hồi. |
| [featured()](../src/main/java/vn/nova/controller/HomeController.java#L35) | Lấy trang sản phẩm có tiêu chí nổi bật. |
| [bestsellers()](../src/main/java/vn/nova/controller/HomeController.java#L41) | Lấy trang sản phẩm bán chạy theo dữ liệu đã ghi nhận. |
| [newArrivals()](../src/main/java/vn/nova/controller/HomeController.java#L46) | Lấy sản phẩm mới về theo dữ liệu phiếu nhập được truy vấn. |

## MediaUploadController

[Mở file](../src/main/java/vn/nova/controller/MediaUploadController.java) — Nhận multipart ảnh đại diện hoặc ảnh sản phẩm; chuyển việc kiểm tra, tải Cloudinary và lưu URL cho MediaUploadService.

| Phương thức | Chức năng |
| --- | --- |
| [MediaUploadController()](../src/main/java/vn/nova/controller/MediaUploadController.java#L20) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [avatar()](../src/main/java/vn/nova/controller/MediaUploadController.java#L24) | Xác thực người dùng rồi xử lý ảnh đại diện được gửi bằng multipart. |
| [productImages()](../src/main/java/vn/nova/controller/MediaUploadController.java#L30) | Kiểm tra yêu cầu và điều phối thêm nhiều ảnh sản phẩm cho ADMIN. |

## SearchHistoryController

[Mở file](../src/main/java/vn/nova/controller/SearchHistoryController.java) — Các endpoint xem, lưu và xóa lịch sử tìm kiếm của người dùng đã đăng nhập.

| Phương thức | Chức năng |
| --- | --- |
| [SearchHistoryController()](../src/main/java/vn/nova/controller/SearchHistoryController.java#L23) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [list()](../src/main/java/vn/nova/controller/SearchHistoryController.java#L30) | Trả danh sách dữ liệu thuộc người dùng hoặc phạm vi được xác thực. |
| [save()](../src/main/java/vn/nova/controller/SearchHistoryController.java#L40) | Xác thực phiên, chuẩn hóa từ khóa và ghi lịch sử tìm kiếm của đúng userId. |
| [clear()](../src/main/java/vn/nova/controller/SearchHistoryController.java#L56) | Xác thực phiên và xóa lịch sử tìm kiếm của chính tài khoản đó. |

## UserController

[Mở file](../src/main/java/vn/nova/controller/UserController.java) — Các endpoint hồ sơ, cập nhật hồ sơ, đổi mật khẩu và đổi email; xác định người dùng qua Bearer token.

| Phương thức | Chức năng |
| --- | --- |
| [UserController()](../src/main/java/vn/nova/controller/UserController.java#L32) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [profile()](../src/main/java/vn/nova/controller/UserController.java#L38) | Trả hồ sơ hiện tại sau khi xác thực người gọi. |
| [update()](../src/main/java/vn/nova/controller/UserController.java#L44) | Nhận yêu cầu cập nhật và chuyển dữ liệu đã validation xuống tầng service. |
| [changePassword()](../src/main/java/vn/nova/controller/UserController.java#L51) | Kiểm tra mật khẩu hiện tại, lưu hash mới và thu hồi phiên để yêu cầu đăng nhập lại. |
| [changeEmail()](../src/main/java/vn/nova/controller/UserController.java#L58) | Kiểm tra email/mật khẩu và phát challenge xác minh tới email mới. |
| [verifyEmailChange()](../src/main/java/vn/nova/controller/UserController.java#L65) | Tiêu thụ challenge đổi email, cập nhật địa chỉ mới và thu hồi phiên cũ. |

## MessageResponse

[Mở file](../src/main/java/vn/nova/dto/MessageResponse.java) — DTO phản hồi ngắn chỉ chứa thông báo cho các thao tác không cần trả đối tượng dữ liệu.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## AdminRequests

[Mở file](../src/main/java/vn/nova/dto/admin/AdminRequests.java) — Tập DTO record đầu vào quản trị; annotation Jakarta Validation mô tả các ràng buộc của từng trường.

| Phương thức | Chức năng |
| --- | --- |
| [AdminRequests()](../src/main/java/vn/nova/dto/admin/AdminRequests.java#L15) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |

## AuthResponse

[Mở file](../src/main/java/vn/nova/dto/auth/AuthResponse.java) — DTO trả token đăng nhập, loại Bearer, thời hạn, thời điểm hết hạn và hồ sơ người dùng.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## ChallengeResponse

[Mở file](../src/main/java/vn/nova/dto/auth/ChallengeResponse.java) — DTO trả thông tin challenge OTP và thông báo; không chứa mã OTP thuần.

| Phương thức | Chức năng |
| --- | --- |
| [withMessage()](../src/main/java/vn/nova/dto/auth/ChallengeResponse.java#L15) | Tạo bản sao DTO có thông báo mới, giữ nguyên dữ liệu challenge. |

## EmailRequest

[Mở file](../src/main/java/vn/nova/dto/auth/EmailRequest.java) — DTO đầu vào email, dùng cho yêu cầu gửi/gửi lại OTP.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## LoginRequest

[Mở file](../src/main/java/vn/nova/dto/auth/LoginRequest.java) — DTO đầu vào email và mật khẩu khi đăng nhập; validation chạy trước service.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## RegisterRequest

[Mở file](../src/main/java/vn/nova/dto/auth/RegisterRequest.java) — DTO đầu vào đăng ký gồm email, mật khẩu, tên và điện thoại cùng các ràng buộc hợp lệ.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## ResetPasswordRequest

[Mở file](../src/main/java/vn/nova/dto/auth/ResetPasswordRequest.java) — DTO đầu vào đặt mật khẩu mới bằng challengeId và mã OTP.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## SessionResponse

[Mở file](../src/main/java/vn/nova/dto/auth/SessionResponse.java) — DTO trả tình trạng phiên đã xác thực và hồ sơ hiện tại.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## VerifyCodeRequest

[Mở file](../src/main/java/vn/nova/dto/auth/VerifyCodeRequest.java) — DTO đầu vào challengeId và mã OTP để xác minh đúng yêu cầu.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## PageResponse

[Mở file](../src/main/java/vn/nova/dto/catalog/PageResponse.java) — DTO phân trang gồm items, total, page, size, totalPages và hasNext, giúp FE đọc danh sách nhất quán.

| Phương thức | Chức năng |
| --- | --- |
| [of()](../src/main/java/vn/nova/dto/catalog/PageResponse.java#L14) | Tạo DTO phân trang và tính totalPages/hasNext từ total, page, size. |

## ChangeEmailRequest

[Mở file](../src/main/java/vn/nova/dto/user/ChangeEmailRequest.java) — DTO đầu vào email mới và mật khẩu xác nhận khi bắt đầu đổi email.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## ChangePasswordRequest

[Mở file](../src/main/java/vn/nova/dto/user/ChangePasswordRequest.java) — DTO đầu vào mật khẩu hiện tại và mật khẩu mới để thay mật khẩu.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## UpdateProfileRequest

[Mở file](../src/main/java/vn/nova/dto/user/UpdateProfileRequest.java) — DTO đầu vào tên, điện thoại, ngày sinh, giới tính và URL avatar tùy chọn khi cập nhật hồ sơ.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## UserProfileResponse

[Mở file](../src/main/java/vn/nova/dto/user/UserProfileResponse.java) — DTO hồ sơ công khai trả về FE; không đưa hash mật khẩu hay thông tin xác thực server ra ngoài.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## ApiException

[Mở file](../src/main/java/vn/nova/exception/ApiException.java) — Lỗi nghiệp vụ mang HTTP status, mã lỗi và thông báo có thể trả về cho client.

| Phương thức | Chức năng |
| --- | --- |
| [ApiException()](../src/main/java/vn/nova/exception/ApiException.java#L13) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [getStatus()](../src/main/java/vn/nova/exception/ApiException.java#L20) | Trả HTTP status được gắn với lỗi nghiệp vụ. |
| [getCode()](../src/main/java/vn/nova/exception/ApiException.java#L25) | Trả mã lỗi ổn định để client nhận diện trường hợp thất bại. |
| [badRequest()](../src/main/java/vn/nova/exception/ApiException.java#L30) | Tạo lỗi yêu cầu không hợp lệ với HTTP status 400. |
| [notFound()](../src/main/java/vn/nova/exception/ApiException.java#L35) | Tạo lỗi 404 khi đối tượng được yêu cầu không tồn tại. |
| [unauthorized()](../src/main/java/vn/nova/exception/ApiException.java#L40) | Tạo lỗi 401 khi thiếu hoặc không còn phiên xác thực hợp lệ. |

## ApiExceptionHandler

[Mở file](../src/main/java/vn/nova/exception/ApiExceptionHandler.java) — Chuyển lỗi nghiệp vụ, validation, xung đột dữ liệu, tệp quá lớn và JDBC thành phản hồi HTTP JSON thống nhất.

| Phương thức | Chức năng |
| --- | --- |
| [handleBusinessError()](../src/main/java/vn/nova/exception/ApiExceptionHandler.java#L29) | Đổi ApiException thành JSON gồm mã lỗi/thông báo cùng HTTP status tương ứng. |
| [handleConflict()](../src/main/java/vn/nova/exception/ApiExceptionHandler.java#L36) | Đổi lỗi toàn vẹn/xung đột dữ liệu thành phản hồi phù hợp, không lộ câu SQL. |
| [handleInvalidInput()](../src/main/java/vn/nova/exception/ApiExceptionHandler.java#L45) | Trả lỗi validation hoặc dữ liệu request không đọc được theo cấu trúc API. |
| [handleLargeImage()](../src/main/java/vn/nova/exception/ApiExceptionHandler.java#L52) | Trả lỗi tệp vượt giới hạn multipart. |
| [handleDatabaseError()](../src/main/java/vn/nova/exception/ApiExceptionHandler.java#L59) | Trả lỗi máy chủ khi JDBC/CSDL không xử lý được, giữ chi tiết nội bộ phía BE. |

## AccountRecord

[Mở file](../src/main/java/vn/nova/model/AccountRecord.java) — Dữ liệu tài khoản đọc từ SQL phục vụ xác thực, gồm trạng thái, vai trò, hash mật khẩu và email đã xác minh.

| Phương thức | Chức năng |
| --- | --- |
| [active()](../src/main/java/vn/nova/model/AccountRecord.java#L11) | Trả true khi trạng thái tài khoản là active. |
| [emailVerified()](../src/main/java/vn/nova/model/AccountRecord.java#L16) | Kiểm tra cờ/thời điểm email đã được xác minh của tài khoản. |

## CatalogFilter

[Mở file](../src/main/java/vn/nova/model/CatalogFilter.java) — Record bộ lọc sản phẩm: chuẩn hóa tham số HTTP, alias danh mục/sắp xếp và kiểm tra giá, cờ, đánh giá, phân trang.

| Phương thức | Chức năng |
| --- | --- |
| [from()](../src/main/java/vn/nova/model/CatalogFilter.java#L21) | Đọc query HTTP, chuẩn hóa alias, kiểm tra khoảng giá/phân trang và tạo record bộ lọc bất biến. |
| [normalizeCategory()](../src/main/java/vn/nova/model/CatalogFilter.java#L53) | Đổi slug/tên alias danh mục từ FE thành mã dùng trong truy vấn. |
| [integer()](../src/main/java/vn/nova/model/CatalogFilter.java#L64) | Đọc số nguyên trong giới hạn và báo lỗi khi tham số sai định dạng. |
| [flag()](../src/main/java/vn/nova/model/CatalogFilter.java#L82) | Đọc cờ boolean và từ chối giá trị khác true/false. |
| [text()](../src/main/java/vn/nova/model/CatalogFilter.java#L94) | Đọc và cắt chuỗi query, áp dụng giới hạn độ dài của bộ lọc. |
| [parseRating()](../src/main/java/vn/nova/model/CatalogFilter.java#L103) | Đọc mức đánh giá tối thiểu và kiểm tra giới hạn hợp lệ. |

## SessionToken

[Mở file](../src/main/java/vn/nova/model/SessionToken.java) — Dữ liệu token phiên vừa cấp kèm thời điểm hết hạn.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## VerificationChallenge

[Mở file](../src/main/java/vn/nova/model/VerificationChallenge.java) — Dữ liệu challenge OTP: chủ sở hữu, đích gửi, mục đích, hạn dùng và trạng thái kiểm tra.

File dữ liệu/record hoặc trạng thái; accessor của record được Java tạo tự động.

## VerificationPurpose

[Mở file](../src/main/java/vn/nova/model/VerificationPurpose.java) — Liệt kê các mục đích xác minh email/khôi phục mật khẩu/đổi email và mã lưu tương ứng trong CSDL.

| Phương thức | Chức năng |
| --- | --- |
| [databaseValue()](../src/main/java/vn/nova/model/VerificationPurpose.java#L20) | Trả mã mục đích OTP dùng khi ghi vào CSDL. |

## AdminRepository

[Mở file](../src/main/java/vn/nova/repository/AdminRepository.java) — Truy vấn và cập nhật dữ liệu quản trị; khóa sản phẩm/cây danh mục, lưu ảnh, SKU, banner và trạng thái tài khoản.

| Phương thức | Chức năng |
| --- | --- |
| [AdminRepository()](../src/main/java/vn/nova/repository/AdminRepository.java#L21) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [isAdmin()](../src/main/java/vn/nova/repository/AdminRepository.java#L24) | Kiểm tra người dùng có vai trò quản trị đang hoạt động. |
| [dashboard()](../src/main/java/vn/nova/repository/AdminRepository.java#L32) | Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL. |
| [productWhere()](../src/main/java/vn/nova/repository/AdminRepository.java#L47) | Dựng phần điều kiện truy vấn quản trị với các nhánh SQL do server xác định. |
| [productParameters()](../src/main/java/vn/nova/repository/AdminRepository.java#L55) | Tạo danh sách tham số theo đúng thứ tự dấu ? trong SQL sản phẩm. |
| [countProducts()](../src/main/java/vn/nova/repository/AdminRepository.java#L61) | Đếm tổng sản phẩm theo cùng điều kiện dùng để lấy danh sách. |
| [products()](../src/main/java/vn/nova/repository/AdminRepository.java#L66) | Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại. |
| [product()](../src/main/java/vn/nova/repository/AdminRepository.java#L82) | Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID. |
| [images()](../src/main/java/vn/nova/repository/AdminRepository.java#L102) | Đọc các URL và thứ tự ảnh hiện có của sản phẩm để kiểm tra giới hạn/chống trùng. |
| [imageLimit()](../src/main/java/vn/nova/repository/AdminRepository.java#L108) | Dựa vào cây danh mục để giới hạn mười ảnh cho nhóm thiết bị chính và năm ảnh cho các nhóm còn lại. |
| [appendImage()](../src/main/java/vn/nova/repository/AdminRepository.java#L119) | Thêm URL ảnh mới vào bộ ảnh với thứ tự tiếp theo của sản phẩm. |
| [lockProduct()](../src/main/java/vn/nova/repository/AdminRepository.java#L128) | Khóa hàng sản phẩm trong giao dịch trước khi tính giới hạn ảnh/cập nhật dữ liệu. |
| [exists()](../src/main/java/vn/nova/repository/AdminRepository.java#L130) | Kiểm tra một bản ghi tham chiếu có tồn tại trước khi ghi dữ liệu liên quan. |
| [createProduct()](../src/main/java/vn/nova/repository/AdminRepository.java#L137) | Kiểm tra/tạo sản phẩm và các dữ liệu liên quan theo tầng controller/repository. |
| [updateProduct()](../src/main/java/vn/nova/repository/AdminRepository.java#L145) | Cập nhật các trường sản phẩm đã được kiểm tra hợp lệ. |
| [deactivateVariants()](../src/main/java/vn/nova/repository/AdminRepository.java#L153) | Ngừng các biến thể cũ trước khi áp dụng bộ SKU cập nhật. |
| [saveVariant()](../src/main/java/vn/nova/repository/AdminRepository.java#L158) | Tạo mới hoặc cập nhật một SKU của sản phẩm. |
| [saveCover()](../src/main/java/vn/nova/repository/AdminRepository.java#L175) | Lưu URL ảnh bìa được chọn của sản phẩm. |
| [archiveProduct()](../src/main/java/vn/nova/repository/AdminRepository.java#L185) | Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ. |
| [categories()](../src/main/java/vn/nova/repository/AdminRepository.java#L188) | Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại. |
| [lockCategoryTree()](../src/main/java/vn/nova/repository/AdminRepository.java#L197) | Lấy khóa khi cập nhật cây danh mục để kiểm tra quan hệ nhất quán. |
| [categoryCycle()](../src/main/java/vn/nova/repository/AdminRepository.java#L203) | Kiểm tra thay đổi cha có tạo vòng lặp trong cây danh mục hay không. |
| [saveCategory()](../src/main/java/vn/nova/repository/AdminRepository.java#L213) | Kiểm tra quan hệ cha/con rồi tạo hoặc cập nhật danh mục. |
| [brands()](../src/main/java/vn/nova/repository/AdminRepository.java#L224) | Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị. |
| [saveBrand()](../src/main/java/vn/nova/repository/AdminRepository.java#L233) | Tạo hoặc cập nhật thương hiệu sau khi chuẩn hóa dữ liệu. |
| [banners()](../src/main/java/vn/nova/repository/AdminRepository.java#L241) | Đọc các banner hợp lệ theo dữ liệu CSDL. |
| [saveBanner()](../src/main/java/vn/nova/repository/AdminRepository.java#L246) | Tạo hoặc cập nhật banner với URL/trạng thái/liên kết sản phẩm hợp lệ. |
| [countUsers()](../src/main/java/vn/nova/repository/AdminRepository.java#L254) | Đếm tổng tài khoản theo bộ lọc của trang quản trị. |
| [users()](../src/main/java/vn/nova/repository/AdminRepository.java#L259) | Lấy danh sách người dùng theo bộ lọc và phân trang quản trị. |
| [like()](../src/main/java/vn/nova/repository/AdminRepository.java#L270) | Escape ký tự đặc biệt của LIKE trước khi dùng làm giá trị tìm kiếm. |
| [userStatus()](../src/main/java/vn/nova/repository/AdminRepository.java#L273) | Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị. |

## CatalogRepository

[Mở file](../src/main/java/vn/nova/repository/CatalogRepository.java) — Các truy vấn JDBC đọc danh mục, thương hiệu, sản phẩm, biến thể, ảnh, thông số và banner trong schema shop.

| Phương thức | Chức năng |
| --- | --- |
| [CatalogRepository()](../src/main/java/vn/nova/repository/CatalogRepository.java#L20) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [findCategories()](../src/main/java/vn/nova/repository/CatalogRepository.java#L25) | Truy vấn danh mục đang hoạt động, ảnh và số sản phẩm phù hợp. |
| [findBrands()](../src/main/java/vn/nova/repository/CatalogRepository.java#L50) | Truy vấn thương hiệu đang hoạt động theo danh mục và số sản phẩm. |
| [countProducts()](../src/main/java/vn/nova/repository/CatalogRepository.java#L65) | Đếm tổng sản phẩm theo cùng điều kiện dùng để lấy danh sách. |
| [findProducts()](../src/main/java/vn/nova/repository/CatalogRepository.java#L71) | Lấy trang sản phẩm với SQL/tham số đã chuẩn hóa và thứ tự đã cho phép. |
| [findProduct()](../src/main/java/vn/nova/repository/CatalogRepository.java#L80) | Đọc dữ liệu sản phẩm theo ID, trả kết quả tùy chọn khi không tồn tại. |
| [findVariants()](../src/main/java/vn/nova/repository/CatalogRepository.java#L94) | Lấy các SKU của sản phẩm với thông tin giá/tồn kho tương ứng. |
| [findImages()](../src/main/java/vn/nova/repository/CatalogRepository.java#L106) | Lấy bộ ảnh sản phẩm theo thứ tự sort_order và ID. |
| [findSpecifications()](../src/main/java/vn/nova/repository/CatalogRepository.java#L114) | Lấy các thông số kỹ thuật gắn với sản phẩm. |
| [findBanners()](../src/main/java/vn/nova/repository/CatalogRepository.java#L124) | Lấy banner đang hoạt động cùng sản phẩm được liên kết. |

## HealthRepository

[Mở file](../src/main/java/vn/nova/repository/HealthRepository.java) — Chạy truy vấn kiểm tra kết nối JDBC đến CSDL.

| Phương thức | Chức năng |
| --- | --- |
| [HealthRepository()](../src/main/java/vn/nova/repository/HealthRepository.java#L17) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [checkDatabase()](../src/main/java/vn/nova/repository/HealthRepository.java#L22) | Chạy truy vấn nhỏ để kiểm tra JDBC/CSDL sẵn sàng. |

## ProductSearchSql

[Mở file](../src/main/java/vn/nova/repository/ProductSearchSql.java) — Dựng CTE, tham số và thứ tự SQL tìm sản phẩm; các tiêu chí giá/RAM/bộ nhớ/tồn kho cùng áp dụng trên tập biến thể hợp lệ.

| Phương thức | Chức năng |
| --- | --- |
| [from()](../src/main/java/vn/nova/repository/ProductSearchSql.java#L15) | Dựng các CTE stock/matching/prices/ratings/cards; ràng buộc tham số bằng dấu ? và chỉ chọn ORDER BY trong danh sách server cho phép. |

## SessionRepository

[Mở file](../src/main/java/vn/nova/repository/SessionRepository.java) — Lưu digest token phiên, tìm người dùng của phiên còn hạn và thu hồi một hoặc mọi phiên.

| Phương thức | Chức năng |
| --- | --- |
| [SessionRepository()](../src/main/java/vn/nova/repository/SessionRepository.java#L17) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [create()](../src/main/java/vn/nova/repository/SessionRepository.java#L22) | Lưu userId, digest của token và thời điểm hết hạn vào bảng phiên. |
| [findAuthenticatedUserId()](../src/main/java/vn/nova/repository/SessionRepository.java#L31) | Tìm userId từ digest phiên còn hạn và tài khoản đang hoạt động. |
| [revoke()](../src/main/java/vn/nova/repository/SessionRepository.java#L42) | Đánh dấu thu hồi một phiên token. |
| [revokeAll()](../src/main/java/vn/nova/repository/SessionRepository.java#L50) | Thu hồi mọi phiên còn hiệu lực của tài khoản. |

## UserRepository

[Mở file](../src/main/java/vn/nova/repository/UserRepository.java) — Các truy vấn JDBC cho tài khoản và hồ sơ: tạo, khóa hàng để cập nhật, đổi email/mật khẩu và lưu URL avatar.

| Phương thức | Chức năng |
| --- | --- |
| [UserRepository()](../src/main/java/vn/nova/repository/UserRepository.java#L21) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [emailExists()](../src/main/java/vn/nova/repository/UserRepository.java#L26) | Kiểm tra có tài khoản đã dùng email được chuẩn hóa hay chưa. |
| [createAccount()](../src/main/java/vn/nova/repository/UserRepository.java#L31) | Chèn tài khoản với hash mật khẩu và thông tin đăng ký. |
| [createAccountDefaults()](../src/main/java/vn/nova/repository/UserRepository.java#L40) | Tạo các dữ liệu mặc định liên quan cho tài khoản vừa đăng ký. |
| [findByEmailForUpdate()](../src/main/java/vn/nova/repository/UserRepository.java#L55) | Đọc và khóa hàng tài khoản theo email trong giao dịch. |
| [findByIdForUpdate()](../src/main/java/vn/nova/repository/UserRepository.java#L63) | Đọc và khóa hàng tài khoản theo ID trong giao dịch. |
| [markEmailVerified()](../src/main/java/vn/nova/repository/UserRepository.java#L71) | Đánh dấu email đã được xác minh khi challenge hợp lệ. |
| [updatePassword()](../src/main/java/vn/nova/repository/UserRepository.java#L79) | Cập nhật hash mật khẩu mới của tài khoản. |
| [updateEmail()](../src/main/java/vn/nova/repository/UserRepository.java#L88) | Cập nhật email sau khi hoàn tất xác minh. |
| [updateProfile()](../src/main/java/vn/nova/repository/UserRepository.java#L96) | Kiểm tra dữ liệu hồ sơ và cập nhật các trường hợp lệ trong giao dịch. |
| [updateAvatar()](../src/main/java/vn/nova/repository/UserRepository.java#L106) | Lưu URL ảnh đại diện vào users.avatar_url. |
| [findProfile()](../src/main/java/vn/nova/repository/UserRepository.java#L111) | Ghép các trường hồ sơ thành UserProfileResponse. |
| [account()](../src/main/java/vn/nova/repository/UserRepository.java#L130) | Ánh xạ một dòng ResultSet tài khoản sang AccountRecord. |
| [text()](../src/main/java/vn/nova/repository/UserRepository.java#L136) | Đọc cột văn bản tùy chọn của ResultSet để ánh xạ hồ sơ. |

## VerificationRepository

[Mở file](../src/main/java/vn/nova/repository/VerificationRepository.java) — Lưu challenge OTP, khóa hàng khi xác minh, tăng số lần thử, tiêu thụ hoặc vô hiệu hóa challenge.

| Phương thức | Chức năng |
| --- | --- |
| [VerificationRepository()](../src/main/java/vn/nova/repository/VerificationRepository.java#L20) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [recentlyIssued()](../src/main/java/vn/nova/repository/VerificationRepository.java#L25) | Kiểm tra challenge vừa được phát để áp dụng thời gian chờ gửi lại. |
| [invalidatePurpose()](../src/main/java/vn/nova/repository/VerificationRepository.java#L34) | Vô hiệu hóa challenge còn dùng được của một mục đích xác minh. |
| [invalidateAll()](../src/main/java/vn/nova/repository/VerificationRepository.java#L42) | Vô hiệu hóa các challenge còn sử dụng được của tài khoản. |
| [create()](../src/main/java/vn/nova/repository/VerificationRepository.java#L50) | Ghi challengeId, mục đích, đích gửi, digest OTP và hạn dùng vào bảng xác minh. |
| [findOwner()](../src/main/java/vn/nova/repository/VerificationRepository.java#L62) | Đọc chủ sở hữu challenge trước bước xác minh có khóa. |
| [findForUpdate()](../src/main/java/vn/nova/repository/VerificationRepository.java#L71) | Lấy và khóa hàng challenge để các lần xác minh không tiêu thụ đồng thời. |
| [incrementAttempts()](../src/main/java/vn/nova/repository/VerificationRepository.java#L86) | Tăng số lần thử mã OTP thất bại. |
| [consume()](../src/main/java/vn/nova/repository/VerificationRepository.java#L91) | Đánh dấu challenge đã được dùng thành công. |

## AdminAccessInterceptor

[Mở file](../src/main/java/vn/nova/security/AdminAccessInterceptor.java) — Đọc Bearer token, xác thực phiên và quyền ADMIN trước khi cho endpoint quản trị chạy.

| Phương thức | Chức năng |
| --- | --- |
| [AdminAccessInterceptor()](../src/main/java/vn/nova/security/AdminAccessInterceptor.java#L22) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [preHandle()](../src/main/java/vn/nova/security/AdminAccessInterceptor.java#L29) | Xác thực người gọi trước controller và gắn userId quản trị vào request. |

## PasswordHasher

[Mở file](../src/main/java/vn/nova/security/PasswordHasher.java) — Băm mật khẩu bằng PBKDF2-HMAC-SHA256 với salt riêng; xác minh hash đã lưu và giữ tương thích mật khẩu cũ.

| Phương thức | Chức năng |
| --- | --- |
| [validate()](../src/main/java/vn/nova/security/PasswordHasher.java#L25) | Áp dụng quy tắc tạo mật khẩu mới; bước đăng nhập dùng matches để vẫn chấp nhận hash mật khẩu cũ hợp lệ. |
| [hash()](../src/main/java/vn/nova/security/PasswordHasher.java#L32) | Tạo salt ngẫu nhiên và dẫn xuất PBKDF2 cho mật khẩu mới hợp lệ. |
| [matches()](../src/main/java/vn/nova/security/PasswordHasher.java#L42) | Đọc tham số/salt từ hash đã lưu và so sánh giá trị dẫn xuất; dữ liệu hash lỗi sẽ không xác thực được. |
| [derive()](../src/main/java/vn/nova/security/PasswordHasher.java#L67) | Dẫn xuất khóa PBKDF2-HMAC-SHA256 từ mật khẩu, salt và số vòng lặp. |

## PasswordRules

[Mở file](../src/main/java/vn/nova/security/PasswordRules.java) — Quy tắc kiểm tra mật khẩu mới: độ dài và các nhóm ký tự bắt buộc; FE và BE dùng cùng yêu cầu.

| Phương thức | Chức năng |
| --- | --- |
| [PasswordRules()](../src/main/java/vn/nova/security/PasswordRules.java#L13) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [isValid()](../src/main/java/vn/nova/security/PasswordRules.java#L16) | Kiểm tra các quy tắc độ dài và nhóm ký tự bắt buộc của mật khẩu mới. |

## RateLimiter

[Mở file](../src/main/java/vn/nova/security/RateLimiter.java) — Giới hạn số yêu cầu theo khóa trong cửa sổ thời gian bằng bộ nhớ của tiến trình backend.

| Phương thức | Chức năng |
| --- | --- |
| [check()](../src/main/java/vn/nova/security/RateLimiter.java#L25) | Đếm yêu cầu theo key trong cửa sổ thời gian và trả lỗi 429 khi vượt ngưỡng. |
| [tooManyRequests()](../src/main/java/vn/nova/security/RateLimiter.java#L42) | Tạo lỗi 429 khi số yêu cầu vượt giới hạn. |

## TokenFactory

[Mở file](../src/main/java/vn/nova/security/TokenFactory.java) — Sinh token ngẫu nhiên, OTP và digest/HMAC; CSDL giữ giá trị dẫn xuất thay cho token phiên thuần.

| Phương thức | Chức năng |
| --- | --- |
| [newToken()](../src/main/java/vn/nova/security/TokenFactory.java#L24) | Sinh token ngẫu nhiên bằng SecureRandom rồi mã hóa Base64 URL-safe. |
| [newOtp()](../src/main/java/vn/nova/security/TokenFactory.java#L31) | Sinh mã OTP bằng nguồn ngẫu nhiên an toàn. |
| [digest()](../src/main/java/vn/nova/security/TokenFactory.java#L36) | Tính SHA-256 để lưu/tra token bằng giá trị băm. |
| [signOtp()](../src/main/java/vn/nova/security/TokenFactory.java#L46) | Ký dữ liệu challenge và OTP bằng HMAC để mã không dùng lại cho challenge khác. |

## AdminService

[Mở file](../src/main/java/vn/nova/service/AdminService.java) — Nghiệp vụ quản trị sản phẩm/danh mục/thương hiệu/banner/người dùng; kiểm tra dữ liệu trước khi cập nhật repository.

| Phương thức | Chức năng |
| --- | --- |
| [AdminService()](../src/main/java/vn/nova/service/AdminService.java#L25) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [dashboard()](../src/main/java/vn/nova/service/AdminService.java#L30) | Đọc các số liệu tổng hợp quản trị từ dữ liệu CSDL. |
| [product()](../src/main/java/vn/nova/service/AdminService.java#L32) | Đọc hoặc hiển thị chi tiết sản phẩm được xác định bằng ID. |
| [categories()](../src/main/java/vn/nova/service/AdminService.java#L34) | Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại. |
| [brands()](../src/main/java/vn/nova/service/AdminService.java#L36) | Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị. |
| [banners()](../src/main/java/vn/nova/service/AdminService.java#L38) | Đọc các banner hợp lệ theo dữ liệu CSDL. |
| [products()](../src/main/java/vn/nova/service/AdminService.java#L41) | Đọc danh sách sản phẩm theo các điều kiện của tầng hiện tại. |
| [saveProduct()](../src/main/java/vn/nova/service/AdminService.java#L51) | Kiểm tra dữ liệu sản phẩm, lưu các trường chính, SKU và ảnh bìa trong giao dịch. |
| [archiveProduct()](../src/main/java/vn/nova/service/AdminService.java#L72) | Chuyển sản phẩm sang trạng thái lưu trữ/ngừng hiển thị theo nghiệp vụ. |
| [saveCategory()](../src/main/java/vn/nova/service/AdminService.java#L75) | Kiểm tra quan hệ cha/con rồi tạo hoặc cập nhật danh mục. |
| [saveBrand()](../src/main/java/vn/nova/service/AdminService.java#L85) | Tạo hoặc cập nhật thương hiệu sau khi chuẩn hóa dữ liệu. |
| [saveBanner()](../src/main/java/vn/nova/service/AdminService.java#L91) | Tạo hoặc cập nhật banner với URL/trạng thái/liên kết sản phẩm hợp lệ. |
| [users()](../src/main/java/vn/nova/service/AdminService.java#L98) | Lấy danh sách người dùng theo bộ lọc và phân trang quản trị. |
| [userStatus()](../src/main/java/vn/nova/service/AdminService.java#L104) | Kiểm tra hoặc cập nhật trạng thái hoạt động của tài khoản theo yêu cầu quản trị. |
| [page()](../src/main/java/vn/nova/service/AdminService.java#L110) | Tạo cấu trúc trang dữ liệu quản trị từ danh sách và số lượng tổng. |
| [pageRules()](../src/main/java/vn/nova/service/AdminService.java#L116) | Kiểm tra page/size trong giới hạn trước khi truy vấn quản trị. |
| [image()](../src/main/java/vn/nova/service/AdminService.java#L121) | Chuẩn hóa/kiểm tra URL ảnh do dữ liệu quản trị cung cấp. |

## AuthService

[Mở file](../src/main/java/vn/nova/service/AuthService.java) — Điều phối đăng ký/đăng nhập; kiểm tra mật khẩu, trạng thái tài khoản, giới hạn tần suất và giao dịch tạo phiên/OTP.

| Phương thức | Chức năng |
| --- | --- |
| [AuthService()](../src/main/java/vn/nova/service/AuthService.java#L39) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [register()](../src/main/java/vn/nova/service/AuthService.java#L54) | Chuẩn hóa dữ liệu đăng ký, tạo tài khoản/challenge OTP hoặc chuyển yêu cầu tới service tương ứng. |
| [login()](../src/main/java/vn/nova/service/AuthService.java#L73) | Kiểm tra thông tin đăng nhập và cấp phản hồi phiên sau khi tài khoản đủ điều kiện. |
| [requestCode()](../src/main/java/vn/nova/service/AuthService.java#L92) | Giới hạn tần suất, tìm tài khoản đủ điều kiện và tạo phản hồi challenge không tiết lộ email có tồn tại hay không. |
| [verifyEmail()](../src/main/java/vn/nova/service/AuthService.java#L108) | Xác minh challenge OTP đăng ký rồi cập nhật trạng thái email và cấp phiên. |
| [verifyResetCode()](../src/main/java/vn/nova/service/AuthService.java#L117) | Kiểm tra OTP khôi phục trước khi cho phép chuyển sang đặt mật khẩu mới. |
| [resetPassword()](../src/main/java/vn/nova/service/AuthService.java#L123) | Xác minh challenge, lưu hash mật khẩu mới và thu hồi các phiên cũ. |
| [currentSession()](../src/main/java/vn/nova/service/AuthService.java#L135) | Đọc hồ sơ của người dùng từ token hợp lệ để xác minh phiên hiện tại. |
| [logout()](../src/main/java/vn/nova/service/AuthService.java#L140) | Thu hồi phiên hiện tại và trả thông báo đăng xuất. |
| [createAuthResponse()](../src/main/java/vn/nova/service/AuthService.java#L145) | Ghép token vừa tạo, thời hạn Bearer và hồ sơ thành AuthResponse. |

## CatalogService

[Mở file](../src/main/java/vn/nova/service/CatalogService.java) — Chuẩn hóa bộ lọc, gọi CatalogRepository và ghép dữ liệu chi tiết gồm biến thể, ảnh, thông số.

| Phương thức | Chức năng |
| --- | --- |
| [CatalogService()](../src/main/java/vn/nova/service/CatalogService.java#L24) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [categories()](../src/main/java/vn/nova/service/CatalogService.java#L29) | Đọc các danh mục theo phạm vi truy vấn của lớp hiện tại. |
| [brands()](../src/main/java/vn/nova/service/CatalogService.java#L45) | Đọc các thương hiệu theo danh mục hoặc phạm vi quản trị. |
| [search()](../src/main/java/vn/nova/service/CatalogService.java#L55) | Chuẩn hóa bộ lọc và trả trang sản phẩm phù hợp. |
| [product()](../src/main/java/vn/nova/service/CatalogService.java#L64) | Đọc sản phẩm đang hoạt động rồi ghép biến thể, ảnh và thông số thành JSON chi tiết. |
| [banners()](../src/main/java/vn/nova/service/CatalogService.java#L73) | Đọc các banner hợp lệ theo dữ liệu CSDL. |

## CloudinaryImageStore

[Mở file](../src/main/java/vn/nova/service/CloudinaryImageStore.java) — Gửi ảnh từ BE tới Cloudinary bằng HTTPS và thông tin xác thực phía server; chỉ chấp nhận URL tài nguyên đúng cloud/publicId.

| Phương thức | Chức năng |
| --- | --- |
| [CloudinaryImageStore()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L32) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [requireConfigured()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L39) | Kiểm tra cloud name, API key và API secret sẵn sàng trước khi gọi Cloudinary. |
| [ownsAsset()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L46) | Kiểm tra URL HTTPS thuộc đúng Cloudinary cloud/publicId gốc; không chấp nhận URL chuyển hướng hoặc đích khác. |
| [upload()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L63) | Gửi ảnh đã được kiểm tra đến điểm nhận upload và chỉ nhận URL trả về hợp lệ. |
| [field()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L96) | Ghi một phần trường văn bản vào body multipart gửi Cloudinary. |
| [unavailable()](../src/main/java/vn/nova/service/CloudinaryImageStore.java#L100) | Tạo lỗi API khi dịch vụ ngoài chưa cấu hình hoặc chưa trả kết quả hợp lệ. |

## EmailService

[Mở file](../src/main/java/vn/nova/service/EmailService.java) — Gửi email OTP qua JavaMailSender; chuyển lỗi SMTP thành lỗi API phù hợp.

| Phương thức | Chức năng |
| --- | --- |
| [EmailService()](../src/main/java/vn/nova/service/EmailService.java#L23) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [sendOtp()](../src/main/java/vn/nova/service/EmailService.java#L30) | Dựng nội dung email và gửi mã qua JavaMailSender/SMTP được cấu hình. |
| [unavailable()](../src/main/java/vn/nova/service/EmailService.java#L52) | Tạo lỗi API khi dịch vụ ngoài chưa cấu hình hoặc chưa trả kết quả hợp lệ. |

## HealthService

[Mở file](../src/main/java/vn/nova/service/HealthService.java) — Tổng hợp tình trạng ứng dụng và kết quả kiểm tra kết nối cơ sở dữ liệu.

| Phương thức | Chức năng |
| --- | --- |
| [HealthService()](../src/main/java/vn/nova/service/HealthService.java#L16) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [check()](../src/main/java/vn/nova/service/HealthService.java#L21) | Gọi repository kiểm tra CSDL rồi ghép phản hồi sức khỏe của ứng dụng. |

## HomeService

[Mở file](../src/main/java/vn/nova/service/HomeService.java) — Ghép dữ liệu Trang chủ và tạo các trang sản phẩm theo tiêu chí nổi bật, bán chạy hoặc mới về.

| Phương thức | Chức năng |
| --- | --- |
| [HomeService()](../src/main/java/vn/nova/service/HomeService.java#L24) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [newArrivals()](../src/main/java/vn/nova/service/HomeService.java#L30) | Lấy sản phẩm mới về theo dữ liệu phiếu nhập được truy vấn. |
| [home()](../src/main/java/vn/nova/service/HomeService.java#L46) | Ghép các nhóm sản phẩm của Trang chủ trong một phản hồi. |
| [featured()](../src/main/java/vn/nova/service/HomeService.java#L63) | Lấy trang sản phẩm có tiêu chí nổi bật. |
| [bestsellers()](../src/main/java/vn/nova/service/HomeService.java#L70) | Lấy trang sản phẩm bán chạy theo dữ liệu đã ghi nhận. |

## ImageUploadValidator

[Mở file](../src/main/java/vn/nova/service/ImageUploadValidator.java) — Kiểm tra nội dung thật của ảnh, loại định dạng, kích thước, giới hạn dung lượng và tạo SHA-256 để nhận diện ảnh trùng.

| Phương thức | Chức năng |
| --- | --- |
| [validate()](../src/main/java/vn/nova/service/ImageUploadValidator.java#L26) | Kiểm tra byte thật của JPG/PNG/WebP, giới hạn kích thước và tạo SHA-256 của nội dung để chống ảnh trùng. |
| [little24()](../src/main/java/vn/nova/service/ImageUploadValidator.java#L75) | Đọc số nguyên 24 bit little-endian trong phần đầu tệp WebP. |
| [ascii()](../src/main/java/vn/nova/service/ImageUploadValidator.java#L77) | So sánh chữ ký byte với nhãn ASCII để nhận dạng nội dung ảnh. |
| [dimensions()](../src/main/java/vn/nova/service/ImageUploadValidator.java#L83) | Kiểm tra giới hạn chiều rộng, chiều cao và tổng số pixel của ảnh. |
| [invalid()](../src/main/java/vn/nova/service/ImageUploadValidator.java#L88) | Tạo lỗi 400 cho dữ liệu ảnh không hợp lệ. |

## MediaUploadService

[Mở file](../src/main/java/vn/nova/service/MediaUploadService.java) — Xác thực người tải ảnh, kiểm tra giới hạn ảnh, chống trùng bằng SHA-256, tải Cloudinary rồi ghi URL vào Neon trong luồng giao dịch.

| Phương thức | Chức năng |
| --- | --- |
| [MediaUploadService()](../src/main/java/vn/nova/service/MediaUploadService.java#L36) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [avatar()](../src/main/java/vn/nova/service/MediaUploadService.java#L43) | Xác thực người dùng rồi xử lý ảnh đại diện được gửi bằng multipart. |
| [avatarInternal()](../src/main/java/vn/nova/service/MediaUploadService.java#L48) | Kiểm tra ảnh, khóa hàng tài khoản, tái dùng ảnh đã có hoặc tải ảnh mới rồi lưu avatar_url. |
| [productImages()](../src/main/java/vn/nova/service/MediaUploadService.java#L66) | Kiểm tra yêu cầu và điều phối thêm nhiều ảnh sản phẩm cho ADMIN. |
| [productImagesInternal()](../src/main/java/vn/nova/service/MediaUploadService.java#L71) | Khóa sản phẩm, loại ảnh trùng, kiểm tra số chỗ còn lại, tải Cloudinary và ghi URL theo thứ tự. |
| [bounded()](../src/main/java/vn/nova/service/MediaUploadService.java#L103) | Giới hạn một tác vụ upload cùng lúc và luôn nhả Semaphore trong finally. |

## SessionService

[Mở file](../src/main/java/vn/nova/service/SessionService.java) — Tạo và xác thực phiên Bearer, đổi token thành userId, kiểm tra hạn dùng và thu hồi phiên.

| Phương thức | Chức năng |
| --- | --- |
| [SessionService()](../src/main/java/vn/nova/service/SessionService.java#L22) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [createSession()](../src/main/java/vn/nova/service/SessionService.java#L28) | Sinh token ngẫu nhiên, lưu digest kèm hạn dùng và trả token thuần đúng lúc cấp phiên. |
| [requireUserId()](../src/main/java/vn/nova/service/SessionService.java#L35) | Tách Bearer token, kiểm tra phiên hợp lệ và trả ID người dùng hoặc lỗi 401. |
| [logout()](../src/main/java/vn/nova/service/SessionService.java#L45) | Thu hồi phiên hiện tại và trả thông báo đăng xuất. |
| [revokeAll()](../src/main/java/vn/nova/service/SessionService.java#L52) | Thu hồi mọi phiên còn hiệu lực của tài khoản. |
| [extractToken()](../src/main/java/vn/nova/service/SessionService.java#L57) | Tách chuỗi token từ header Authorization theo định dạng Bearer. |

## UserService

[Mở file](../src/main/java/vn/nova/service/UserService.java) — Xử lý hồ sơ, đổi mật khẩu/email, thu hồi phiên khi cần và kiểm tra URL ảnh đại diện.

| Phương thức | Chức năng |
| --- | --- |
| [UserService()](../src/main/java/vn/nova/service/UserService.java#L36) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [getProfile()](../src/main/java/vn/nova/service/UserService.java#L47) | Xác thực Bearer token rồi đọc hồ sơ của chủ phiên. |
| [getProfileById()](../src/main/java/vn/nova/service/UserService.java#L52) | Đọc DTO hồ sơ theo userId đã được nghiệp vụ xác định. |
| [updateProfile()](../src/main/java/vn/nova/service/UserService.java#L57) | Kiểm tra dữ liệu hồ sơ và cập nhật các trường hợp lệ trong giao dịch. |
| [changePassword()](../src/main/java/vn/nova/service/UserService.java#L76) | Kiểm tra mật khẩu hiện tại, lưu hash mới và thu hồi phiên để yêu cầu đăng nhập lại. |
| [changeEmail()](../src/main/java/vn/nova/service/UserService.java#L92) | Kiểm tra email/mật khẩu và phát challenge xác minh tới email mới. |
| [verifyEmailChange()](../src/main/java/vn/nova/service/UserService.java#L109) | Tiêu thụ challenge đổi email, cập nhật địa chỉ mới và thu hồi phiên cũ. |
| [validateAvatar()](../src/main/java/vn/nova/service/UserService.java#L124) | Kiểm tra URL ảnh đại diện theo các điều kiện server chấp nhận. |

## VerificationService

[Mở file](../src/main/java/vn/nova/service/VerificationService.java) — Phát và kiểm tra challenge OTP theo mục đích; quản lý hạn dùng, số lần thử, tiêu thụ mã và giao dịch xác minh.

| Phương thức | Chức năng |
| --- | --- |
| [VerificationService()](../src/main/java/vn/nova/service/VerificationService.java#L38) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [issueLocked()](../src/main/java/vn/nova/service/VerificationService.java#L54) | Tạo OTP cho tài khoản đã khóa trong giao dịch, lưu digest/hạn dùng và gửi email. |
| [genericChallenge()](../src/main/java/vn/nova/service/VerificationService.java#L70) | Trả challenge giả có cùng kiểu phản hồi để tránh tiết lộ trạng thái tài khoản. |
| [invalidateAll()](../src/main/java/vn/nova/service/VerificationService.java#L76) | Vô hiệu hóa các challenge còn sử dụng được của tài khoản. |
| [verify()](../src/main/java/vn/nova/service/VerificationService.java#L81) | Kiểm tra và tiêu thụ OTP theo mục đích rồi thực hiện nghiệp vụ nằm trong callback. |
| [checkRecoveryCode()](../src/main/java/vn/nova/service/VerificationService.java#L88) | Kiểm tra mã khôi phục còn hợp lệ mà chưa đổi mật khẩu ngay. |
| [check()](../src/main/java/vn/nova/service/VerificationService.java#L93) | Khóa challenge, kiểm tra mục đích/hạn dùng/số lần thử và so sánh digest OTP trước khi tiêu thụ. |
| [failed()](../src/main/java/vn/nova/service/VerificationService.java#L139) | Ghi nhận số lần nhập sai và tạo lỗi xác minh phù hợp. |
| [invalidCode()](../src/main/java/vn/nova/service/VerificationService.java#L144) | Tạo lỗi chung khi mã sai, hết hạn hoặc challenge không còn dùng được. |

## JdbcExecutor

[Mở file](../src/main/java/vn/nova/util/JdbcExecutor.java) — Lớp bọc JdbcTemplate để truy vấn danh sách/một dòng/giá trị số hoặc cập nhật với tham số ràng buộc.

| Phương thức | Chức năng |
| --- | --- |
| [JdbcExecutor()](../src/main/java/vn/nova/util/JdbcExecutor.java#L26) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [findMany()](../src/main/java/vn/nova/util/JdbcExecutor.java#L31) | Chạy SQL có tham số và ánh xạ toàn bộ các dòng kết quả. |
| [findOptional()](../src/main/java/vn/nova/util/JdbcExecutor.java#L52) | Chạy truy vấn kỳ vọng tối đa một dòng, trả Optional khi chưa có dữ liệu. |
| [findOne()](../src/main/java/vn/nova/util/JdbcExecutor.java#L57) | Lấy một dòng bắt buộc và chuyển trường hợp không có thành lỗi phù hợp. |
| [findLong()](../src/main/java/vn/nova/util/JdbcExecutor.java#L62) | Đọc một giá trị số nguyên dài từ truy vấn. |
| [update()](../src/main/java/vn/nova/util/JdbcExecutor.java#L69) | Chạy câu lệnh INSERT/UPDATE/DELETE với giá trị truyền bằng tham số JDBC. |

## RequestRules

[Mở file](../src/main/java/vn/nova/util/RequestRules.java) — Chuẩn hóa email/số điện thoại và chuyển chuỗi trống thành null trước khi xử lý nghiệp vụ.

| Phương thức | Chức năng |
| --- | --- |
| [RequestRules()](../src/main/java/vn/nova/util/RequestRules.java#L13) | Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ. |
| [normalizeEmail()](../src/main/java/vn/nova/util/RequestRules.java#L17) | Cắt khoảng trắng và chuẩn hóa email trước khi lưu/tìm kiếm. |
| [normalizePhone()](../src/main/java/vn/nova/util/RequestRules.java#L22) | Chuẩn hóa và kiểm tra định dạng số điện thoại. |
| [nullIfBlank()](../src/main/java/vn/nova/util/RequestRules.java#L31) | Đổi chuỗi trống thành null để ghi các trường tùy chọn. |
