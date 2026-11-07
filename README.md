# NOVA_BE_GITHUB — mã nguồn backend để đọc

Gói độc lập chứa mã Java/Spring Boot, JDBC/SQL, cấu hình mẫu và tài nguyên backend NOVA_GK.
Mã frontend Android nằm riêng trong gói NOVA_FE_GITHUB.

- `src/main/java/vn/nova/controller`: HTTP endpoint.
- `src/main/java/vn/nova/service`: nghiệp vụ và dịch vụ ngoài.
- `src/main/java/vn/nova/repository`: truy vấn JDBC tới schema shop.
- `src/main/resources/application.properties`: cấu hình đọc biến môi trường.
- `database`: SQL đã có trong bản nguồn GK; không phải bản sao dữ liệu Neon.

Các file Java được giữ nguyên chú thích tiếng Việt. Gói không kèm Python, kiểm thử hoặc JAR ứng dụng.
URL backend đang được Android sử dụng vẫn là `https://nova-uit.japaneast.cloudapp.azure.com/api`.
Mật khẩu Neon/SMTP và Cloudinary API Secret vẫn nằm trong cấu hình riêng của môi trường đang dùng.

## Tài liệu riêng của BE

- [Cây thư mục có giải thích từng file](CAU-TRUC-BE.md)
- [Chức năng từng file](docs/CHUC-NANG-TUNG-FILE.md)
- [Tra cứu phương thức](docs/TRA-CUU-HAM.md)
- [Giải thích code](docs/GIAI-THICH-CODE.md)
- [Giải thích CSDL và sơ đồ ERD](docs/GIAI-THICH-CSDL.md)
