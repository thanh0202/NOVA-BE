# Giải thích code BE

Gói này chỉ chứa backend tham khảo. Các mô tả Android giải thích cách client gọi API; mã FE ở gói riêng.

## 1. Gọi HTTP và nhận JSON

Một thao tác FE đi theo đường: View → Screen → ApiClient → endpoint BE → service → repository → Neon → JSON → callback → View.
`ApiClient` chạy HTTP và giải mã JSON trong `ExecutorService`, không chặn main thread.
Sau khi có kết quả, `Handler(Looper.getMainLooper())` chuyển callback về luồng được phép sửa View.
Nếu có token, request thêm `Authorization: Bearer ...`; khi token hết hạn, app xử lý mã 401 và xóa phiên phù hợp.

Các endpoint danh sách có thể trả mảng trực tiếp hoặc đối tượng phân trang.
`BaseScreen.arr` đọc `JSONArray`, hoặc lấy `items` từ `PageResponse`, tránh hiểu nhầm đối tượng thành mảng rỗng.
`ApiClient.encode` mã hóa query UTF-8 để từ khóa tiếng Việt và dấu cách không làm sai URL.

## 2. Đăng ký, OTP và đăng nhập

Đăng ký: `RegisterScreen` kiểm tra biểu mẫu → `POST /auth/register` → `RegisterRequest` được validation → `AuthService.register`.
BE chuẩn hóa email/điện thoại, băm mật khẩu, tạo tài khoản chưa xác minh và challenge OTP trong luồng giao dịch.
`VerificationService` quản lý mã/hạn dùng/số lần thử; `EmailService` gửi email bằng SMTP.
Xác minh email thành công cập nhật tài khoản và tạo phiên. Đăng nhập chỉ cấp phiên cho tài khoản hoạt động, email đủ điều kiện và mật khẩu đúng.

`PasswordHasher` dùng PBKDF2-HMAC-SHA256 với salt ngẫu nhiên; hai tài khoản dùng cùng mật khẩu vẫn có hash khác nhau.
`matches` đọc tham số trong hash đã lưu để xác minh; không áp quy tắc tạo mật khẩu mới lên mật khẩu cũ đang đăng nhập.
`TokenFactory` sinh token ngẫu nhiên và lưu digest qua `SessionRepository`; OTP được gắn với challenge bằng HMAC.
Khi mã OTP sai, bộ đếm được commit trước khi lỗi được ném ra ngoài để tránh rollback mất số lần nhập sai.

Ở Android, `SecureSession` mã hóa token bằng AES/GCM trước khi ghi preferences, dùng khóa trong Android Keystore.
Nếu không chọn ghi nhớ, token chỉ được giữ tạm trong bộ nhớ tiến trình. App không lưu mật khẩu tài khoản.

## 3. Tìm kiếm và SQL bộ lọc

`SearchScreen` theo dõi văn bản, hủy callback tìm kiếm cũ và chờ một khoảng trước khi gọi gợi ý để hạn chế gửi request theo từng ký tự.
Chỉ dữ liệu của từ khóa/phiên bản hiện tại được nhận. Chạm gợi ý sản phẩm mở chi tiết; xác nhận từ khóa mở `SearchResultsScreen`.
`SearchResultsScreen` dùng xử lý của `CatalogScreen`; bộ lọc được chuyển thành query HTTP.

BE dùng `CatalogFilter.from` kiểm tra page/size, khoảng giá, số nguyên, boolean, rating và alias.
`ProductSearchSql` dựng các CTE `stock`, `matching`, `prices`, `ratings`, `cards`.
Các tiêu chí giá/RAM/bộ nhớ/tồn kho đều lọc trên `matching`, rồi mới tính giá và ghép sản phẩm.
Vì vậy một SKU rẻ không thể thỏa giá tối thiểu còn SKU khác thỏa giá tối đa để làm sản phẩm lọt sai khoảng giá.
Danh mục được mở rộng qua view `v_category_descendants`; giá trị client đi bằng tham số `?`, còn `ORDER BY` lấy từ lựa chọn server cho phép.

## 4. Đổi avatar và thêm ảnh sản phẩm

`ProfileScreen` hoặc `AdminImagesScreen` gọi `ImagePicker`; bộ chọn hệ thống chỉ cấp quyền với các URI đã chọn.
`UploadImage.jpeg` đọc ảnh vào tệp cache với giới hạn, kiểm tra kích thước trước khi giải mã đầy đủ, xoay EXIF, thu nhỏ, nén JPEG và dọn bitmap/tệp tạm.
`ApiClient.uploadImages` truyền multipart trên luồng nền, có Bearer token. Trường `file` dành cho avatar, `files` cho nhiều ảnh sản phẩm.

`MediaUploadController` nhận multipart. `ImageUploadValidator` kiểm tra byte thực, định dạng, số pixel/dung lượng và tính SHA-256.
`MediaUploadService` xác thực người gọi, kiểm tra số chỗ, khóa hàng tài khoản/sản phẩm và chống trùng theo hash.
`CloudinaryImageStore` gọi HTTPS từ server bằng credential đọc từ cấu hình BE; Android không nhận API Secret.
Cloudinary giữ byte ảnh, Neon lưu URL trong `users.avatar_url` hoặc `product_images.url`.
Giới hạn số ảnh được lấy theo nhóm sản phẩm; quá số chỗ trả lỗi trước khi thêm URL vào CSDL.

## 5. Đọc backend theo các tầng

| Tầng/nhóm | Cách đọc |
| --- | --- |
| Controller | Annotation chỉ ra HTTP method/URL; DTO và param cho biết client phải gửi gì. |
| Service | Theo dõi kiểm tra đầu vào, xác thực, transaction và các lời gọi repository/dịch vụ ngoài. |
| Repository | Đọc câu SQL, alias JSON và thứ tự tham số; `FOR UPDATE`/khóa giúp tránh cập nhật đồng thời sai. |
| DTO record | Tên component là trường JSON; annotation validation ràng buộc request. Accessor được Java sinh tự động. |
| Exception handler | Xem HTTP status, mã lỗi và thông báo mà FE nhận khi một bước thất bại. |

`TransactionTemplate.execute` bao một nhóm đọc/ghi cần nhất quán; callback ném lỗi thường dẫn tới rollback.
Upload Cloudinary là lời gọi ngoài CSDL; ảnh đã lưu bên ngoài không tự bị PostgreSQL rollback.
Code dùng publicId theo SHA để lần thử lại tái dùng đúng tài nguyên và tránh xóa nhầm ảnh có thể đang được sử dụng.

## 6. Kết nối hiện có và phạm vi GK

URL mặc định của FE vẫn là `https://nova-uit.japaneast.cloudapp.azure.com/api`.
Để chạy FE với cấu hình đó không cần chạy BE trên máy. `novaApiUrl` và `novaMidtermApi` nằm trong Gradle module app.
FE có mã cho các luồng mở rộng như giỏ hàng, đơn, thanh toán, đổi trả; sự có mặt của Screen không bảo đảm backend GK có đủ endpoint cho luồng ấy.
Đọc `BuildConfig.NOVA_MIDTERM_API`, các nhánh điều kiện trong Screen và `MainActivity.isFullEditionRoute` khi phân biệt chế độ.

`application.properties` đọc `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD` và các biến SMTP/OTP/Cloudinary từ môi trường.
Mẫu `.env.example` giữ địa chỉ Neon với mật khẩu giả định. Trong gói BE tham khảo đã bỏ launcher Python, Spring Boot không tự đọc file `.env`
hoặc tự đổi `NEON_DATABASE_URL` thành JDBC; nếu tự triển khai sau này phải cấp các biến JDBC đúng cho tiến trình Java.
Các cấu hình thật và dịch vụ đang chạy được giữ ngoài gói này.

File SQL `017_home_featured.sql` chỉ đánh dấu một nhóm sản phẩm có sẵn là nổi bật sau bước kiểm tra điều kiện.
Nó không tạo schema `shop` và không phải toàn bộ CSDL của đồ án; `spring.sql.init.mode=never` cũng không tự chạy file SQL đó.

## 7. Các endpoint có trong mã backend đang giữ

Các URL sau được trích từ annotation trong controller của bản nguồn; đây là danh sách code, không phải kết quả thử HTTP trên server.

| HTTP | Đường dẫn | File / phương thức |
| --- | --- | --- |
| GET | `/api/admin/dashboard` | `AdminController.java` → `dashboard()` |
| GET | `/api/admin/products` | `AdminController.java` → `products()` |
| GET | `/api/admin/products/{id}` | `AdminController.java` → `product()` |
| POST | `/api/admin/products` | `AdminController.java` → `createProduct()` |
| PUT | `/api/admin/products/{id}` | `AdminController.java` → `updateProduct()` |
| DELETE | `/api/admin/products/{id}` | `AdminController.java` → `archiveProduct()` |
| GET | `/api/admin/categories` | `AdminController.java` → `categories()` |
| POST | `/api/admin/categories` | `AdminController.java` → `createCategory()` |
| PUT | `/api/admin/categories/{id}` | `AdminController.java` → `updateCategory()` |
| GET | `/api/admin/brands` | `AdminController.java` → `brands()` |
| POST | `/api/admin/brands` | `AdminController.java` → `createBrand()` |
| PUT | `/api/admin/brands/{id}` | `AdminController.java` → `updateBrand()` |
| GET | `/api/admin/banners` | `AdminController.java` → `banners()` |
| POST | `/api/admin/banners` | `AdminController.java` → `createBanner()` |
| PUT | `/api/admin/banners/{id}` | `AdminController.java` → `updateBanner()` |
| GET | `/api/admin/users` | `AdminController.java` → `users()` |
| PUT | `/api/admin/users/{id}/status` | `AdminController.java` → `userStatus()` |
| POST | `/api/auth/register` | `AuthController.java` → `register()` |
| POST | `/api/auth/login` | `AuthController.java` → `login()` |
| POST | `/api/auth/verify-email` | `AuthController.java` → `verifyEmail()` |
| POST | `/api/auth/resend-verification` | `AuthController.java` → `resend()` |
| POST | `/api/auth/forgot-password` | `AuthController.java` → `forgotPassword()` |
| POST | `/api/auth/verify-reset-code` | `AuthController.java` → `verifyResetCode()` |
| POST | `/api/auth/reset-password` | `AuthController.java` → `resetPassword()` |
| GET | `/api/auth/session` | `AuthController.java` → `currentSession()` |
| POST | `/api/auth/logout` | `AuthController.java` → `logout()` |
| GET | `/api/products` | `CatalogController.java` → `search()` |
| GET | `/api/products/search` | `CatalogController.java` → `search()` |
| GET | `/api/products/{id}` | `CatalogController.java` → `product()` |
| GET | `/api/banners` | `CatalogController.java` → `banners()` |
| GET | `/api/categories` | `CatalogController.java` → `categories()` |
| GET | `/api/brands` | `CatalogController.java` → `brands()` |
| GET | `/api/health` | `HealthController.java` → `check()` |
| GET | `/api/home` | `HomeController.java` → `home()` |
| GET | `/api/home/featured` | `HomeController.java` → `featured()` |
| GET | `/api/home/bestsellers` | `HomeController.java` → `bestsellers()` |
| GET | `/api/home/new-arrivals` | `HomeController.java` → `newArrivals()` |
| POST | `/api/me/avatar` | `MediaUploadController.java` → `avatar()` |
| POST | `/api/users/me/avatar` | `MediaUploadController.java` → `avatar()` |
| POST | `/api/admin/products/{id}/images` | `MediaUploadController.java` → `productImages()` |
| GET | `/api/search-history` | `SearchHistoryController.java` → `list()` |
| POST | `/api/search-history` | `SearchHistoryController.java` → `save()` |
| DELETE | `/api/search-history` | `SearchHistoryController.java` → `clear()` |
| GET | `/api/me` | `UserController.java` → `profile()` |
| GET | `/api/users/me` | `UserController.java` → `profile()` |
| PUT | `/api/me` | `UserController.java` → `update()` |
| PUT | `/api/users/me` | `UserController.java` → `update()` |
| PUT | `/api/me/password` | `UserController.java` → `changePassword()` |
| PUT | `/api/users/me/password` | `UserController.java` → `changePassword()` |
| POST | `/api/me/email` | `UserController.java` → `changeEmail()` |
| POST | `/api/users/me/email` | `UserController.java` → `changeEmail()` |
| POST | `/api/me/email/verify` | `UserController.java` → `verifyEmailChange()` |
| POST | `/api/users/me/email/verify` | `UserController.java` → `verifyEmailChange()` |

## 8. Các đối tượng SQL được code tham chiếu

`shop.auth_sessions`, `shop.brands`, `shop.carts`, `shop.categories`, `shop.home_banners`, `shop.inventory_balances`, `shop.membership_accounts`, `shop.membership_tiers`, `shop.notification_preferences`, `shop.orders`, `shop.product_images`, `shop.product_specifications`, `shop.product_variants`, `shop.products`, `shop.reviews`, `shop.roles`, `shop.search_history`, `shop.specification_definitions`, `shop.user_roles`, `shop.users`, `shop.v_category_descendants`, `shop.v_product_bestsellers`, `shop.v_product_new_arrivals`, `shop.verification_challenges`.

Danh sách trên có cả bảng và view. Chỉ từ mã Java không thể kết luận đã có toàn bộ dữ liệu hay mọi đối tượng trên một database mới.

## 9. Ý nghĩa các loại comment

- Đầu file Java: mục đích file, bố cục liên quan và tầng chịu trách nhiệm.
- Trước phương thức: dữ liệu/thao tác chính và kết quả cần hiểu khi đọc hàm.
- Bên trong đoạn khó: luồng nền/main thread, generation, bộ lọc cũ, salt/hash, transaction, byte ảnh/URL ảnh.
- XML: mục đích tài nguyên; vùng layout quan trọng có thêm ghi chú về Java/ID sử dụng.
- Gradle/properties/TOML/SQL: vai trò cấu hình và ảnh hưởng tới cách build/chạy.

JSON, font, bitmap và JAR được giữ đúng định dạng; cách dùng được ghi trong bảng chức năng từng file.
Các chú thích mới giải thích code hiện có, không thay đổi thuật toán, endpoint, dữ liệu hay giao diện.
