# Từ điển dữ liệu Neon — schema shop

Nguồn: đọc trực tiếp catalog Neon tại 07/11/2026 20:53 (UTC+7). Chỉ xuất cấu trúc, không xuất các hàng dữ liệu ứng dụng.

PK = khóa chính; FK = khóa ngoại; UK = ràng buộc duy nhất một cột. Các UNIQUE ghép/INDEX một phần xem mục ràng buộc và chỉ mục của bảng.

## Tài khoản và xác thực

### shop.auth_sessions

Phiên xác thực: lưu hash token, thiết bị, hạn dùng và thời điểm thu hồi.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `refresh_token_hash` | `character(64)` | Không | UK | Không khai báo | Băm SHA-256 của refresh token có entropy cao; không lưu token rõ. |
| `device_name` | `character varying(150)` | Có | — | Không khai báo | Thuộc tính device_name của bảng auth_sessions; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `ip_address` | `inet` | Có | — | Không khai báo | Thuộc tính ip_address của bảng auth_sessions; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `expires_at` | `timestamp with time zone` | Không | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `revoked_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm thu hồi phiên; logout và đổi mật khẩu cập nhật trường này. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `auth_sessions_check`: `CHECK (expires_at > created_at)`.
- `auth_sessions_created_at_not_null`: `NOT NULL created_at`.
- `auth_sessions_expires_at_not_null`: `NOT NULL expires_at`.
- `auth_sessions_id_not_null`: `NOT NULL id`.
- `auth_sessions_pkey`: `PRIMARY KEY (id)`.
- `auth_sessions_refresh_token_hash_key`: `UNIQUE (refresh_token_hash)`.
- `auth_sessions_refresh_token_hash_not_null`: `NOT NULL refresh_token_hash`.
- `auth_sessions_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `auth_sessions_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `auth_sessions_pkey`: `CREATE UNIQUE INDEX auth_sessions_pkey ON shop.auth_sessions USING btree (id)`.
- `auth_sessions_refresh_token_hash_key`: `CREATE UNIQUE INDEX auth_sessions_refresh_token_hash_key ON shop.auth_sessions USING btree (refresh_token_hash)`.
- `ix_fk_auth_sessions_user_id`: `CREATE INDEX ix_fk_auth_sessions_user_id ON shop.auth_sessions USING btree (user_id)`.
- `ix_nova_active_session`: `CREATE INDEX ix_nova_active_session ON shop.auth_sessions USING btree (user_id, expires_at) WHERE (revoked_at IS NULL)`.

### shop.roles

Danh mục vai trò để phân quyền tài khoản.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(40)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(100)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |

**Ràng buộc trong CSDL**

- `roles_code_key`: `UNIQUE (code)`.
- `roles_code_not_null`: `NOT NULL code`.
- `roles_id_not_null`: `NOT NULL id`.
- `roles_name_not_null`: `NOT NULL name`.
- `roles_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `roles_code_key`: `CREATE UNIQUE INDEX roles_code_key ON shop.roles USING btree (code)`.
- `roles_pkey`: `CREATE UNIQUE INDEX roles_pkey ON shop.roles USING btree (id)`.

### shop.user_addresses

Sổ địa chỉ của người dùng, gồm người nhận, vị trí và cờ địa chỉ mặc định.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `recipient_name` | `character varying(150)` | Không | — | Không khai báo | Tên người nhận hàng; ở đơn hàng đây là bản sao chốt. |
| `recipient_phone` | `character varying(20)` | Không | — | Không khai báo | Số điện thoại người nhận hàng, lưu dạng văn bản. |
| `address_line` | `text` | Không | — | Không khai báo | Số nhà, đường và địa chỉ chi tiết; không suy luận từ định danh hành chính. |
| `address_type` | `character varying(20)` | Không | — | 'other'::character varying | Loại địa chỉ hiển thị: home là nhà, work là văn phòng, other là địa chỉ khác. |
| `label` | `character varying(100)` | Có | — | Không khai báo | Nhãn địa chỉ tùy chọn do khách đặt, ví dụ Nhà riêng hoặc Văn phòng. |
| `ward` | `character varying(120)` | Có | — | Không khai báo | Tên phường/xã tại thời điểm ghi địa chỉ, có thể NULL khi chưa có dữ liệu. |
| `district` | `character varying(120)` | Có | — | Không khai báo | Tên quận/huyện nếu địa chỉ nguồn sử dụng; tùy chọn để hỗ trợ dữ liệu hành chính cũ. |
| `province` | `character varying(120)` | Không | — | Không khai báo | Tên tỉnh hoặc thành phố theo địa chỉ nhận hàng. |
| `country_code` | `character(2)` | Không | — | 'VN'::bpchar | Mã quốc gia hai ký tự, mặc định VN. |
| `is_default` | `boolean` | Không | — | false | Địa chỉ mặc định của tài khoản; mỗi người chỉ có tối đa một bản ghi true. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `delivery_note` | `text` | Có | — | Không khai báo | Ghi chú giao hàng. |

**Ràng buộc trong CSDL**

- `user_addresses_address_line_not_null`: `NOT NULL address_line`.
- `user_addresses_address_type_check`: `CHECK (address_type::text = ANY (ARRAY['home'::character varying, 'work'::character varying, 'other'::character varying]::text[]))`.
- `user_addresses_address_type_not_null`: `NOT NULL address_type`.
- `user_addresses_country_code_not_null`: `NOT NULL country_code`.
- `user_addresses_created_at_not_null`: `NOT NULL created_at`.
- `user_addresses_id_not_null`: `NOT NULL id`.
- `user_addresses_id_user_id_key`: `UNIQUE (id, user_id)`.
- `user_addresses_is_default_not_null`: `NOT NULL is_default`.
- `user_addresses_pkey`: `PRIMARY KEY (id)`.
- `user_addresses_province_not_null`: `NOT NULL province`.
- `user_addresses_recipient_name_not_null`: `NOT NULL recipient_name`.
- `user_addresses_recipient_phone_not_null`: `NOT NULL recipient_phone`.
- `user_addresses_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `user_addresses_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `user_addresses_id_user_id_key`: `CREATE UNIQUE INDEX user_addresses_id_user_id_key ON shop.user_addresses USING btree (id, user_id)`.
- `user_addresses_pkey`: `CREATE UNIQUE INDEX user_addresses_pkey ON shop.user_addresses USING btree (id)`.
- `ux_address_default`: `CREATE UNIQUE INDEX ux_address_default ON shop.user_addresses USING btree (user_id) WHERE is_default`.

### shop.user_roles

Bảng nối nhiều-nhiều giữa tài khoản và vai trò; khóa chính ghép ngăn gán trùng cùng vai trò.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `user_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `role_id` | `bigint` | Không | PK, FK | Không khai báo | Thuộc tính role_id của bảng user_roles; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `assigned_at` | `timestamp with time zone` | Không | — | now() | Thuộc tính assigned_at của bảng user_roles; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `user_roles_assigned_at_not_null`: `NOT NULL assigned_at`.
- `user_roles_pkey`: `PRIMARY KEY (user_id, role_id)`.
- `user_roles_role_id_fkey`: `FOREIGN KEY (role_id) REFERENCES shop.roles(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `user_roles_role_id_not_null`: `NOT NULL role_id`.
- `user_roles_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `user_roles_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_user_roles_role_id`: `CREATE INDEX ix_fk_user_roles_role_id ON shop.user_roles USING btree (role_id)`.
- `user_roles_pkey`: `CREATE UNIQUE INDEX user_roles_pkey ON shop.user_roles USING btree (user_id, role_id)`.

### shop.users

Tài khoản và hồ sơ: email, hash mật khẩu, họ tên, avatar, trạng thái, xác minh email và ngày sinh.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `email` | `character varying(254)` | Không | — | Không khai báo | Địa chỉ email; users yêu cầu lowercase và trim trước khi ghi. |
| `password_hash` | `text` | Không | — | Không khai báo | Mật khẩu được băm bằng BCrypt hoặc Argon2id; không lưu mật khẩu rõ. |
| `full_name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị của người dùng; không dùng làm định danh đăng nhập. |
| `phone` | `character varying(20)` | Có | — | Không khai báo | Số điện thoại dạng văn bản để giữ số 0 đầu hoặc mã quốc gia. |
| `avatar_url` | `text` | Có | — | Không khai báo | URL ảnh đại diện; backend kiểm loại file, kích thước và quyền sử dụng. |
| `gender` | `character varying(20)` | Có | — | Không khai báo | Giới tính tự khai: male, female, other hoặc undisclosed; NULL nếu chưa cung cấp. |
| `status` | `character varying(20)` | Không | — | 'active'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `email_verified_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính email_verified_at của bảng users; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `password_changed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính password_changed_at của bảng users; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |
| `birth_date` | `date` | Có | — | Không khai báo | Ngày sinh tùy chọn, API dùng định dạng ISO yyyy-MM-dd. |

**Ràng buộc trong CSDL**

- `users_birth_date_minimum`: `CHECK (birth_date IS NULL OR birth_date >= '1900-01-01'::date)`.
- `users_created_at_not_null`: `NOT NULL created_at`.
- `users_email_check`: `CHECK (email::text = lower(TRIM(BOTH FROM email)))`.
- `users_email_not_null`: `NOT NULL email`.
- `users_full_name_not_null`: `NOT NULL full_name`.
- `users_gender_check`: `CHECK (gender::text = ANY (ARRAY['male'::character varying, 'female'::character varying, 'other'::character varying, 'undisclosed'::character varying]::text[]))`.
- `users_id_not_null`: `NOT NULL id`.
- `users_password_hash_check`: `CHECK (length(password_hash) >= 30)`.
- `users_password_hash_not_null`: `NOT NULL password_hash`.
- `users_pkey`: `PRIMARY KEY (id)`.
- `users_status_check`: `CHECK (status::text = ANY (ARRAY['active'::character varying, 'locked'::character varying, 'disabled'::character varying]::text[]))`.
- `users_status_not_null`: `NOT NULL status`.
- `users_updated_at_not_null`: `NOT NULL updated_at`.

**Chỉ mục**

- `users_pkey`: `CREATE UNIQUE INDEX users_pkey ON shop.users USING btree (id)`.
- `ux_users_email`: `CREATE UNIQUE INDEX ux_users_email ON shop.users USING btree (email)`.

**Trigger**

- `trg_touch_users`: `CREATE TRIGGER trg_touch_users BEFORE UPDATE ON shop.users FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.verification_challenges

Challenge OTP theo mục đích; lưu hash/HMAC mã, số lần thử, hạn dùng và trạng thái tiêu thụ.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `destination` | `character varying(254)` | Không | — | Không khai báo | Địa chỉ nhận challenge; backend phải kiểm và che bớt khi trả API. |
| `purpose` | `character varying(30)` | Không | — | Không khai báo | Mục đích challenge: reset mật khẩu, xác minh email hoặc đổi email. |
| `token_hash` | `character(64)` | Không | — | Không khai báo | Băm HMAC-SHA256 của OTP/token; khóa HMAC nằm ngoài CSDL. |
| `attempts` | `smallint` | Không | — | 0 | Số lần thử hoặc lần xử lý; giới hạn theo CHECK của bảng. |
| `expires_at` | `timestamp with time zone` | Không | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `consumed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm đã dùng challenge; sau khi có giá trị không được dùng lại. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `challenge_id` | `character varying(64)` | Có | — | Không khai báo | Random public challenge identifier. OTP is stored only as HMAC bound to this identifier. |

**Ràng buộc trong CSDL**

- `verification_challenges_attempts_check`: `CHECK (attempts >= 0 AND attempts <= 5)`.
- `verification_challenges_attempts_not_null`: `NOT NULL attempts`.
- `verification_challenges_check`: `CHECK (expires_at > created_at)`.
- `verification_challenges_created_at_not_null`: `NOT NULL created_at`.
- `verification_challenges_destination_not_null`: `NOT NULL destination`.
- `verification_challenges_expires_at_not_null`: `NOT NULL expires_at`.
- `verification_challenges_id_not_null`: `NOT NULL id`.
- `verification_challenges_pkey`: `PRIMARY KEY (id)`.
- `verification_challenges_purpose_check`: `CHECK (purpose::text = ANY (ARRAY['password_reset'::character varying, 'email_verify'::character varying, 'email_change'::character varying]::text[]))`.
- `verification_challenges_purpose_not_null`: `NOT NULL purpose`.
- `verification_challenges_token_hash_not_null`: `NOT NULL token_hash`.
- `verification_challenges_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.

**Chỉ mục**

- `ix_fk_verification_challenges_user_id`: `CREATE INDEX ix_fk_verification_challenges_user_id ON shop.verification_challenges USING btree (user_id)`.
- `ix_nova_challenge_destination`: `CREATE INDEX ix_nova_challenge_destination ON shop.verification_challenges USING btree (destination, purpose, created_at DESC)`.
- `ux_nova_challenge_id`: `CREATE UNIQUE INDEX ux_nova_challenge_id ON shop.verification_challenges USING btree (challenge_id) WHERE (challenge_id IS NOT NULL)`.
- `verification_challenges_pkey`: `CREATE UNIQUE INDEX verification_challenges_pkey ON shop.verification_challenges USING btree (id)`.

## Danh mục, sản phẩm và đánh giá

### shop.brands

Thương hiệu sản phẩm với mã, tên, logo và trạng thái hoạt động.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(100)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `logo_url` | `text` | Có | — | Không khai báo | Thuộc tính logo_url của bảng brands; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng brands; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `brands_code_key`: `UNIQUE (code)`.
- `brands_code_not_null`: `NOT NULL code`.
- `brands_id_not_null`: `NOT NULL id`.
- `brands_is_active_not_null`: `NOT NULL is_active`.
- `brands_name_not_null`: `NOT NULL name`.
- `brands_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `brands_code_key`: `CREATE UNIQUE INDEX brands_code_key ON shop.brands USING btree (code)`.
- `brands_pkey`: `CREATE UNIQUE INDEX brands_pkey ON shop.brands USING btree (id)`.

### shop.categories

Cây danh mục tự tham chiếu qua parent_id; có ảnh và thứ tự hiển thị.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(120)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `parent_id` | `bigint` | Có | FK | Không khai báo | Danh mục cha; backend kiểm không tạo chu trình nhiều cấp. |
| `sort_order` | `integer` | Không | — | 0 | Thứ tự hiển thị tăng dần trong phạm vi đối tượng cha. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng categories; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `image_url` | `text` | Có | — | Không khai báo | Ảnh danh mục dùng chung trong ứng dụng. URL HTTPS hoặc /media/...; ưu tiên ảnh quản trị viên đặt. |

**Ràng buộc trong CSDL**

- `categories_check`: `CHECK (parent_id IS DISTINCT FROM id)`.
- `categories_code_key`: `UNIQUE (code)`.
- `categories_code_not_null`: `NOT NULL code`.
- `categories_id_not_null`: `NOT NULL id`.
- `categories_is_active_not_null`: `NOT NULL is_active`.
- `categories_name_not_null`: `NOT NULL name`.
- `categories_parent_id_fkey`: `FOREIGN KEY (parent_id) REFERENCES shop.categories(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `categories_pkey`: `PRIMARY KEY (id)`.
- `categories_sort_order_not_null`: `NOT NULL sort_order`.

**Chỉ mục**

- `categories_code_key`: `CREATE UNIQUE INDEX categories_code_key ON shop.categories USING btree (code)`.
- `categories_pkey`: `CREATE UNIQUE INDEX categories_pkey ON shop.categories USING btree (id)`.
- `ix_fk_categories_parent_id`: `CREATE INDEX ix_fk_categories_parent_id ON shop.categories USING btree (parent_id)`.

### shop.data_sources

Nguồn tham khảo/công bố dữ liệu sản phẩm hoặc giá.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `base_url` | `text` | Có | — | Không khai báo | Địa chỉ website gốc của nguồn tham khảo. |
| `notes` | `text` | Có | — | Không khai báo | Thuộc tính notes của bảng data_sources; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `data_sources_code_key`: `UNIQUE (code)`.
- `data_sources_code_not_null`: `NOT NULL code`.
- `data_sources_id_not_null`: `NOT NULL id`.
- `data_sources_name_not_null`: `NOT NULL name`.
- `data_sources_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `data_sources_code_key`: `CREATE UNIQUE INDEX data_sources_code_key ON shop.data_sources USING btree (code)`.
- `data_sources_pkey`: `CREATE UNIQUE INDEX data_sources_pkey ON shop.data_sources USING btree (id)`.

### shop.favorites

Các cặp người dùng/sản phẩm đã đánh dấu yêu thích.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `user_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `product_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `favorites_created_at_not_null`: `NOT NULL created_at`.
- `favorites_pkey`: `PRIMARY KEY (user_id, product_id)`.
- `favorites_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `favorites_product_id_not_null`: `NOT NULL product_id`.
- `favorites_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `favorites_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `favorites_pkey`: `CREATE UNIQUE INDEX favorites_pkey ON shop.favorites USING btree (user_id, product_id)`.
- `ix_fk_favorites_product_id`: `CREATE INDEX ix_fk_favorites_product_id ON shop.favorites USING btree (product_id)`.

### shop.home_banners

Banner Trang chủ với URL ảnh và sản phẩm được mở khi chạm.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Định danh bản ghi; xem PK và cột IDENTITY để biết cách sinh giá trị. |
| `title` | `character varying(200)` | Không | — | Không khai báo | Tiêu đề hiển thị. |
| `image_url` | `text` | Không | — | Không khai báo | URL ảnh của đối tượng. |
| `product_id` | `bigint` | Có | FK | Không khai báo | Sản phẩm liên quan; tham chiếu cụ thể xem FK. |
| `is_active` | `boolean` | Không | — | true | Cờ bật/tắt sử dụng hoặc hiển thị đối tượng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo bản ghi. |

**Ràng buộc trong CSDL**

- `home_banners_created_at_not_null`: `NOT NULL created_at`.
- `home_banners_id_not_null`: `NOT NULL id`.
- `home_banners_image_url_not_null`: `NOT NULL image_url`.
- `home_banners_is_active_not_null`: `NOT NULL is_active`.
- `home_banners_pkey`: `PRIMARY KEY (id)`.
- `home_banners_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `home_banners_title_not_null`: `NOT NULL title`.

**Chỉ mục**

- `home_banners_pkey`: `CREATE UNIQUE INDEX home_banners_pkey ON shop.home_banners USING btree (id)`.

### shop.price_observations

Lịch sử ghi nhận giá của SKU từ nguồn, kèm URL và thời điểm quan sát.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `source_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến đơn vị/website nguồn của dữ liệu quan sát. |
| `source_url` | `text` | Không | — | Không khai báo | URL nguồn tham khảo để truy xuất dữ liệu và kiểm tra lại. |
| `price_vnd` | `bigint` | Không | — | Không khai báo | Giá bán nguyên đồng Việt Nam; không dùng số thực. |
| `observed_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm ghi nhận dữ liệu nguồn; không bảo đảm còn đúng ở hiện tại. |
| `kind` | `character varying(20)` | Không | — | 'observed'::character varying | Loại dữ liệu/nghiệp vụ được giới hạn bằng CHECK của bảng. |
| `note` | `text` | Có | — | Không khai báo | Thuộc tính note của bảng price_observations; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `price_observations_id_not_null`: `NOT NULL id`.
- `price_observations_kind_check`: `CHECK (kind::text = ANY (ARRAY['observed'::character varying, 'demo'::character varying]::text[]))`.
- `price_observations_kind_not_null`: `NOT NULL kind`.
- `price_observations_observed_at_not_null`: `NOT NULL observed_at`.
- `price_observations_pkey`: `PRIMARY KEY (id)`.
- `price_observations_price_vnd_check`: `CHECK (price_vnd >= 0)`.
- `price_observations_price_vnd_not_null`: `NOT NULL price_vnd`.
- `price_observations_source_id_fkey`: `FOREIGN KEY (source_id) REFERENCES shop.data_sources(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `price_observations_source_url_not_null`: `NOT NULL source_url`.
- `price_observations_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `price_observations_variant_id_not_null`: `NOT NULL variant_id`.

**Chỉ mục**

- `ix_fk_price_observations_source_id`: `CREATE INDEX ix_fk_price_observations_source_id ON shop.price_observations USING btree (source_id)`.
- `ix_price_observations`: `CREATE INDEX ix_price_observations ON shop.price_observations USING btree (variant_id, observed_at DESC)`.
- `price_observations_pkey`: `CREATE UNIQUE INDEX price_observations_pkey ON shop.price_observations USING btree (id)`.

### shop.product_images

URL ảnh sản phẩm, tùy chọn gắn SKU, alt text và thứ tự hiển thị; không lưu byte ảnh.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `product_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `variant_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `url` | `text` | Không | — | Không khai báo | URL file hoặc ảnh; không chứa mã truy cập bí mật trong dữ liệu công khai. |
| `alt_text` | `text` | Không | — | Không khai báo | Văn bản thay thế mô tả ảnh cho khả năng truy cập. |
| `sort_order` | `integer` | Không | — | 0 | Thứ tự hiển thị tăng dần trong phạm vi đối tượng cha. |
| `source_url` | `text` | Có | — | Không khai báo | URL nguồn tham khảo để truy xuất dữ liệu và kiểm tra lại. |

**Ràng buộc trong CSDL**

- `product_images_alt_text_not_null`: `NOT NULL alt_text`.
- `product_images_id_not_null`: `NOT NULL id`.
- `product_images_pkey`: `PRIMARY KEY (id)`.
- `product_images_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `product_images_product_id_not_null`: `NOT NULL product_id`.
- `product_images_sort_order_not_null`: `NOT NULL sort_order`.
- `product_images_url_not_null`: `NOT NULL url`.
- `product_images_variant_id_product_id_fkey`: `FOREIGN KEY (variant_id, product_id) REFERENCES shop.product_variants(id, product_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.

**Chỉ mục**

- `ix_fk_product_images_product_id`: `CREATE INDEX ix_fk_product_images_product_id ON shop.product_images USING btree (product_id)`.
- `ix_fk_product_images_variant_id`: `CREATE INDEX ix_fk_product_images_variant_id ON shop.product_images USING btree (variant_id)`.
- `product_images_pkey`: `CREATE UNIQUE INDEX product_images_pkey ON shop.product_images USING btree (id)`.

### shop.product_specifications

Giá trị thông số của từng sản phẩm; nối sản phẩm với loại thông số.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `product_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `specification_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến định nghĩa thông số kỹ thuật dùng chung. |
| `value_text` | `text` | Không | — | Không khai báo | Giá trị thông số kỹ thuật dạng văn bản đúng nguồn. |

**Ràng buộc trong CSDL**

- `product_specifications_pkey`: `PRIMARY KEY (product_id, specification_id)`.
- `product_specifications_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `product_specifications_product_id_not_null`: `NOT NULL product_id`.
- `product_specifications_specification_id_fkey`: `FOREIGN KEY (specification_id) REFERENCES shop.specification_definitions(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `product_specifications_specification_id_not_null`: `NOT NULL specification_id`.
- `product_specifications_value_text_not_null`: `NOT NULL value_text`.

**Chỉ mục**

- `ix_fk_product_specifications_specification_id`: `CREATE INDEX ix_fk_product_specifications_specification_id ON shop.product_specifications USING btree (specification_id)`.
- `product_specifications_pkey`: `CREATE UNIQUE INDEX product_specifications_pkey ON shop.product_specifications USING btree (product_id, specification_id)`.

### shop.product_variants

SKU bán hàng: màu, RAM, bộ nhớ, giá hiện tại/niêm yết và trạng thái.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `product_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `sku` | `character varying(100)` | Không | UK | Không khai báo | Mã duy nhất của một phiên bản sản phẩm có thể bán và quản lý tồn kho. |
| `color` | `character varying(100)` | Không | — | Không khai báo | Tên màu của SKU; ghi đúng dữ liệu nguồn hoặc đánh dấu giả lập. |
| `storage_gb` | `integer` | Có | — | Không khai báo | Dung lượng bộ nhớ lưu trữ tính GB; NULL nếu không áp dụng hoặc chưa biết. |
| `ram_gb` | `integer` | Có | — | Không khai báo | Dung lượng RAM tính GB; NULL nếu không áp dụng hoặc chưa biết. |
| `price_vnd` | `bigint` | Không | — | Không khai báo | Giá bán nguyên đồng Việt Nam; không dùng số thực. |
| `list_price_vnd` | `bigint` | Có | — | Không khai báo | Giá niêm yết tham chiếu; NULL khi nguồn không cung cấp. |
| `price_kind` | `character varying(20)` | Không | — | 'demo'::character varying | observed: giá tham khảo nguồn; demo: giá giả lập phục vụ đồ án. |
| `status` | `character varying(20)` | Không | — | 'active'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `product_variants_check`: `CHECK (list_price_vnd IS NULL OR list_price_vnd >= price_vnd)`.
- `product_variants_color_not_null`: `NOT NULL color`.
- `product_variants_created_at_not_null`: `NOT NULL created_at`.
- `product_variants_id_not_null`: `NOT NULL id`.
- `product_variants_id_product_id_key`: `UNIQUE (id, product_id)`.
- `product_variants_list_price_vnd_check`: `CHECK (list_price_vnd >= 0)`.
- `product_variants_pkey`: `PRIMARY KEY (id)`.
- `product_variants_price_kind_check`: `CHECK (price_kind::text = ANY (ARRAY['observed'::character varying, 'demo'::character varying]::text[]))`.
- `product_variants_price_kind_not_null`: `NOT NULL price_kind`.
- `product_variants_price_vnd_check`: `CHECK (price_vnd >= 0)`.
- `product_variants_price_vnd_not_null`: `NOT NULL price_vnd`.
- `product_variants_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `product_variants_product_id_not_null`: `NOT NULL product_id`.
- `product_variants_ram_gb_check`: `CHECK (ram_gb > 0)`.
- `product_variants_sku_key`: `UNIQUE (sku)`.
- `product_variants_sku_not_null`: `NOT NULL sku`.
- `product_variants_status_check`: `CHECK (status::text = ANY (ARRAY['active'::character varying, 'inactive'::character varying]::text[]))`.
- `product_variants_status_not_null`: `NOT NULL status`.
- `product_variants_storage_gb_check`: `CHECK (storage_gb > 0)`.
- `product_variants_updated_at_not_null`: `NOT NULL updated_at`.

**Chỉ mục**

- `ix_variants_product`: `CREATE INDEX ix_variants_product ON shop.product_variants USING btree (product_id, status)`.
- `product_variants_id_product_id_key`: `CREATE UNIQUE INDEX product_variants_id_product_id_key ON shop.product_variants USING btree (id, product_id)`.
- `product_variants_pkey`: `CREATE UNIQUE INDEX product_variants_pkey ON shop.product_variants USING btree (id)`.
- `product_variants_sku_key`: `CREATE UNIQUE INDEX product_variants_sku_key ON shop.product_variants USING btree (sku)`.

**Trigger**

- `trg_touch_product_variants`: `CREATE TRIGGER trg_touch_product_variants BEFORE UPDATE ON shop.product_variants FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.products

Thông tin chung sản phẩm, thương hiệu, danh mục, mô tả và cờ nổi bật.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(80)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `brand_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến thương hiệu chính thức của sản phẩm. |
| `category_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến danh mục chính hoặc phạm vi danh mục của ưu đãi. |
| `name` | `character varying(250)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `slug` | `character varying(300)` | Không | UK | Không khai báo | Chuỗi URL duy nhất, chuẩn hóa từ tên; đổi tên không tự đổi slug lịch sử. |
| `summary` | `text` | Có | — | Không khai báo | Mô tả ngắn phục vụ danh sách và tìm kiếm. |
| `description` | `text` | Có | — | Không khai báo | Nội dung mô tả; backend làm sạch HTML trước khi hiển thị nếu có. |
| `source_url` | `text` | Có | — | Không khai báo | URL nguồn tham khảo để truy xuất dữ liệu và kiểm tra lại. |
| `observed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm ghi nhận dữ liệu nguồn; không bảo đảm còn đúng ở hiện tại. |
| `is_featured` | `boolean` | Không | — | false | Sản phẩm nổi bật do admin chọn, tách biệt số liệu bán chạy. |
| `status` | `character varying(20)` | Không | — | 'active'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `products_brand_id_fkey`: `FOREIGN KEY (brand_id) REFERENCES shop.brands(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `products_brand_id_not_null`: `NOT NULL brand_id`.
- `products_category_id_fkey`: `FOREIGN KEY (category_id) REFERENCES shop.categories(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `products_category_id_not_null`: `NOT NULL category_id`.
- `products_code_key`: `UNIQUE (code)`.
- `products_code_not_null`: `NOT NULL code`.
- `products_created_at_not_null`: `NOT NULL created_at`.
- `products_id_not_null`: `NOT NULL id`.
- `products_is_featured_not_null`: `NOT NULL is_featured`.
- `products_name_not_null`: `NOT NULL name`.
- `products_pkey`: `PRIMARY KEY (id)`.
- `products_slug_key`: `UNIQUE (slug)`.
- `products_slug_not_null`: `NOT NULL slug`.
- `products_status_check`: `CHECK (status::text = ANY (ARRAY['active'::character varying, 'draft'::character varying, 'archived'::character varying]::text[]))`.
- `products_status_not_null`: `NOT NULL status`.
- `products_updated_at_not_null`: `NOT NULL updated_at`.

**Chỉ mục**

- `ix_products_brand`: `CREATE INDEX ix_products_brand ON shop.products USING btree (brand_id, status)`.
- `ix_products_category`: `CREATE INDEX ix_products_category ON shop.products USING btree (category_id, status)`.
- `ix_products_search`: `CREATE INDEX ix_products_search ON shop.products USING gin (to_tsvector('simple'::regconfig, (((name)::text || ' '::text) || COALESCE(summary, ''::text))))`.
- `products_code_key`: `CREATE UNIQUE INDEX products_code_key ON shop.products USING btree (code)`.
- `products_pkey`: `CREATE UNIQUE INDEX products_pkey ON shop.products USING btree (id)`.
- `products_slug_key`: `CREATE UNIQUE INDEX products_slug_key ON shop.products USING btree (slug)`.

**Trigger**

- `trg_touch_products`: `CREATE TRIGGER trg_touch_products BEFORE UPDATE ON shop.products FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.review_images

URL các ảnh đính kèm một đánh giá.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `review_id` | `bigint` | Không | FK | Không khai báo | Thuộc tính review_id của bảng review_images; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `url` | `text` | Không | — | Không khai báo | URL file hoặc ảnh; không chứa mã truy cập bí mật trong dữ liệu công khai. |
| `sort_order` | `integer` | Không | — | 0 | Thứ tự hiển thị tăng dần trong phạm vi đối tượng cha. |

**Ràng buộc trong CSDL**

- `review_images_id_not_null`: `NOT NULL id`.
- `review_images_pkey`: `PRIMARY KEY (id)`.
- `review_images_review_id_fkey`: `FOREIGN KEY (review_id) REFERENCES shop.reviews(id) ON DELETE CASCADE`; ON DELETE CASCADE, ON UPDATE NO ACTION.
- `review_images_review_id_not_null`: `NOT NULL review_id`.
- `review_images_sort_order_not_null`: `NOT NULL sort_order`.
- `review_images_url_not_null`: `NOT NULL url`.

**Chỉ mục**

- `ix_fk_review_images_review_id`: `CREATE INDEX ix_fk_review_images_review_id ON shop.review_images USING btree (review_id)`.
- `review_images_pkey`: `CREATE UNIQUE INDEX review_images_pkey ON shop.review_images USING btree (id)`.

### shop.reviews

Đánh giá sản phẩm, số sao và tham chiếu món đã mua để kiểm tra quyền đánh giá.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `product_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `order_item_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `rating` | `smallint` | Không | — | Không khai báo | Số sao nguyên từ 1 đến 5, không nhận số sao ngoài miền. |
| `title` | `character varying(200)` | Có | — | Không khai báo | Tiêu đề ngắn hiển thị của đánh giá hoặc thông báo. |
| `content` | `text` | Không | — | Không khai báo | Nội dung đánh giá do người dùng nhập, qua kiểm duyệt trước publish. |
| `status` | `character varying(20)` | Không | — | 'pending'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `reviews_content_not_null`: `NOT NULL content`.
- `reviews_created_at_not_null`: `NOT NULL created_at`.
- `reviews_id_not_null`: `NOT NULL id`.
- `reviews_order_item_id_product_id_fkey`: `FOREIGN KEY (order_item_id, product_id) REFERENCES shop.order_items(id, product_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `reviews_pkey`: `PRIMARY KEY (id)`.
- `reviews_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `reviews_product_id_not_null`: `NOT NULL product_id`.
- `reviews_rating_check`: `CHECK (rating >= 1 AND rating <= 5)`.
- `reviews_rating_not_null`: `NOT NULL rating`.
- `reviews_status_check`: `CHECK (status::text = ANY (ARRAY['pending'::character varying, 'published'::character varying, 'hidden'::character varying]::text[]))`.
- `reviews_status_not_null`: `NOT NULL status`.
- `reviews_updated_at_not_null`: `NOT NULL updated_at`.
- `reviews_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `reviews_user_id_not_null`: `NOT NULL user_id`.
- `reviews_user_id_product_id_key`: `UNIQUE (user_id, product_id)`.

**Chỉ mục**

- `ix_fk_reviews_order_item_id`: `CREATE INDEX ix_fk_reviews_order_item_id ON shop.reviews USING btree (order_item_id)`.
- `ix_reviews_product_status`: `CREATE INDEX ix_reviews_product_status ON shop.reviews USING btree (product_id, status, created_at DESC)`.
- `reviews_pkey`: `CREATE UNIQUE INDEX reviews_pkey ON shop.reviews USING btree (id)`.
- `reviews_user_id_product_id_key`: `CREATE UNIQUE INDEX reviews_user_id_product_id_key ON shop.reviews USING btree (user_id, product_id)`.

**Trigger**

- `trg_review_purchase`: `CREATE TRIGGER trg_review_purchase BEFORE INSERT OR UPDATE ON shop.reviews FOR EACH ROW EXECUTE FUNCTION shop.validate_review_purchase()`.
- `trg_touch_reviews`: `CREATE TRIGGER trg_touch_reviews BEFORE UPDATE ON shop.reviews FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.specification_definitions

Danh mục các loại thông số kỹ thuật và đơn vị.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(80)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `unit` | `character varying(40)` | Có | — | Không khai báo | Đơn vị thông số; NULL nếu giá trị văn bản đã bao gồm đơn vị hoặc không áp dụng. |
| `sort_order` | `integer` | Không | — | 0 | Thứ tự hiển thị tăng dần trong phạm vi đối tượng cha. |

**Ràng buộc trong CSDL**

- `specification_definitions_code_key`: `UNIQUE (code)`.
- `specification_definitions_code_not_null`: `NOT NULL code`.
- `specification_definitions_id_not_null`: `NOT NULL id`.
- `specification_definitions_name_not_null`: `NOT NULL name`.
- `specification_definitions_pkey`: `PRIMARY KEY (id)`.
- `specification_definitions_sort_order_not_null`: `NOT NULL sort_order`.

**Chỉ mục**

- `specification_definitions_code_key`: `CREATE UNIQUE INDEX specification_definitions_code_key ON shop.specification_definitions USING btree (code)`.
- `specification_definitions_pkey`: `CREATE UNIQUE INDEX specification_definitions_pkey ON shop.specification_definitions USING btree (id)`.

## Kho và nhập hàng

### shop.inventory_balances

Số tồn on_hand và số đang giữ reserved cho từng cặp kho/SKU.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `warehouse_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến kho xử lý tồn và giữ hàng của đơn. |
| `variant_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `on_hand` | `integer` | Không | — | 0 | Số lượng vật lý còn trong kho, bao gồm hàng đang giữ. |
| `reserved` | `integer` | Không | — | 0 | Số lượng đã giữ cho đơn; khả dụng bằng on_hand trừ reserved. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `inventory_balances_check`: `CHECK (reserved <= on_hand)`.
- `inventory_balances_on_hand_check`: `CHECK (on_hand >= 0)`.
- `inventory_balances_on_hand_not_null`: `NOT NULL on_hand`.
- `inventory_balances_pkey`: `PRIMARY KEY (warehouse_id, variant_id)`.
- `inventory_balances_reserved_check`: `CHECK (reserved >= 0)`.
- `inventory_balances_reserved_not_null`: `NOT NULL reserved`.
- `inventory_balances_updated_at_not_null`: `NOT NULL updated_at`.
- `inventory_balances_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_balances_variant_id_not_null`: `NOT NULL variant_id`.
- `inventory_balances_warehouse_id_fkey`: `FOREIGN KEY (warehouse_id) REFERENCES shop.warehouses(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_balances_warehouse_id_not_null`: `NOT NULL warehouse_id`.

**Chỉ mục**

- `inventory_balances_pkey`: `CREATE UNIQUE INDEX inventory_balances_pkey ON shop.inventory_balances USING btree (warehouse_id, variant_id)`.
- `ix_fk_inventory_balances_variant_id`: `CREATE INDEX ix_fk_inventory_balances_variant_id ON shop.inventory_balances USING btree (variant_id)`.

**Trigger**

- `trg_touch_inventory_balances`: `CREATE TRIGGER trg_touch_inventory_balances BEFORE UPDATE ON shop.inventory_balances FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.inventory_movements

Nhật ký tăng/giảm kho, liên kết phiếu nhập hoặc món đơn hàng và khóa chống ghi lặp.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `warehouse_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến kho xử lý tồn và giữ hàng của đơn. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `quantity_delta` | `integer` | Không | — | Không khai báo | Biến động số lượng có dấu: nhập/hoàn dương, xuất bán âm. |
| `reason` | `character varying(30)` | Không | — | Không khai báo | Thuộc tính reason của bảng inventory_movements; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `receipt_item_id` | `bigint` | Có | FK | Không khai báo | Dòng phiếu nhập tạo biến động kho; bắt buộc cho reason receipt. |
| `order_item_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `idempotency_key` | `character varying(160)` | Không | UK | Không khai báo | Khóa chống thực hiện trùng nghiệp vụ khi gửi lại yêu cầu. |
| `created_by` | `bigint` | Có | FK | Không khai báo | Thuộc tính created_by của bảng inventory_movements; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `note` | `text` | Có | — | Không khai báo | Thuộc tính note của bảng inventory_movements; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `fk_inventory_movement_order_item`: `FOREIGN KEY (order_item_id) REFERENCES shop.order_items(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_movements_check`: `CHECK ((reason::text = 'receipt'::text) = (receipt_item_id IS NOT NULL))`.
- `inventory_movements_created_at_not_null`: `NOT NULL created_at`.
- `inventory_movements_created_by_fkey`: `FOREIGN KEY (created_by) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_movements_id_not_null`: `NOT NULL id`.
- `inventory_movements_idempotency_key_key`: `UNIQUE (idempotency_key)`.
- `inventory_movements_idempotency_key_not_null`: `NOT NULL idempotency_key`.
- `inventory_movements_pkey`: `PRIMARY KEY (id)`.
- `inventory_movements_quantity_delta_check`: `CHECK (quantity_delta <> 0)`.
- `inventory_movements_quantity_delta_not_null`: `NOT NULL quantity_delta`.
- `inventory_movements_reason_check`: `CHECK (reason::text = ANY (ARRAY['receipt'::character varying, 'sale'::character varying, 'return'::character varying, 'adjustment'::character varying]::text[]))`.
- `inventory_movements_reason_not_null`: `NOT NULL reason`.
- `inventory_movements_receipt_item_id_fkey`: `FOREIGN KEY (receipt_item_id) REFERENCES shop.stock_receipt_items(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_movements_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_movements_variant_id_not_null`: `NOT NULL variant_id`.
- `inventory_movements_warehouse_id_fkey`: `FOREIGN KEY (warehouse_id) REFERENCES shop.warehouses(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_movements_warehouse_id_not_null`: `NOT NULL warehouse_id`.

**Chỉ mục**

- `inventory_movements_idempotency_key_key`: `CREATE UNIQUE INDEX inventory_movements_idempotency_key_key ON shop.inventory_movements USING btree (idempotency_key)`.
- `inventory_movements_pkey`: `CREATE UNIQUE INDEX inventory_movements_pkey ON shop.inventory_movements USING btree (id)`.
- `ix_fk_inventory_movements_created_by`: `CREATE INDEX ix_fk_inventory_movements_created_by ON shop.inventory_movements USING btree (created_by)`.
- `ix_fk_inventory_movements_order_item_id`: `CREATE INDEX ix_fk_inventory_movements_order_item_id ON shop.inventory_movements USING btree (order_item_id)`.
- `ix_fk_inventory_movements_receipt_item_id`: `CREATE INDEX ix_fk_inventory_movements_receipt_item_id ON shop.inventory_movements USING btree (receipt_item_id)`.
- `ix_fk_inventory_movements_variant_id`: `CREATE INDEX ix_fk_inventory_movements_variant_id ON shop.inventory_movements USING btree (variant_id)`.
- `ix_fk_inventory_movements_warehouse_id`: `CREATE INDEX ix_fk_inventory_movements_warehouse_id ON shop.inventory_movements USING btree (warehouse_id)`.

### shop.inventory_reservations

Giữ số lượng theo món đơn hàng, SKU và kho trong thời gian xử lý đơn.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_item_id` | `bigint` | Không | FK, UK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `warehouse_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến kho xử lý tồn và giữ hàng của đơn. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `status` | `character varying(20)` | Không | — | 'held'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `expires_at` | `timestamp with time zone` | Có | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `inventory_reservations_created_at_not_null`: `NOT NULL created_at`.
- `inventory_reservations_id_not_null`: `NOT NULL id`.
- `inventory_reservations_order_item_id_key`: `UNIQUE (order_item_id)`.
- `inventory_reservations_order_item_id_not_null`: `NOT NULL order_item_id`.
- `inventory_reservations_order_item_id_variant_id_fkey`: `FOREIGN KEY (order_item_id, variant_id) REFERENCES shop.order_items(id, variant_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_reservations_pkey`: `PRIMARY KEY (id)`.
- `inventory_reservations_quantity_check`: `CHECK (quantity > 0)`.
- `inventory_reservations_quantity_not_null`: `NOT NULL quantity`.
- `inventory_reservations_status_check`: `CHECK (status::text = ANY (ARRAY['held'::character varying, 'consumed'::character varying, 'released'::character varying]::text[]))`.
- `inventory_reservations_status_not_null`: `NOT NULL status`.
- `inventory_reservations_variant_id_not_null`: `NOT NULL variant_id`.
- `inventory_reservations_warehouse_id_fkey`: `FOREIGN KEY (warehouse_id) REFERENCES shop.warehouses(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `inventory_reservations_warehouse_id_not_null`: `NOT NULL warehouse_id`.
- `inventory_reservations_warehouse_id_variant_id_fkey`: `FOREIGN KEY (warehouse_id, variant_id) REFERENCES shop.inventory_balances(warehouse_id, variant_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.

**Chỉ mục**

- `inventory_reservations_order_item_id_key`: `CREATE UNIQUE INDEX inventory_reservations_order_item_id_key ON shop.inventory_reservations USING btree (order_item_id)`.
- `inventory_reservations_pkey`: `CREATE UNIQUE INDEX inventory_reservations_pkey ON shop.inventory_reservations USING btree (id)`.
- `ix_fk_inventory_reservations_warehouse_id`: `CREATE INDEX ix_fk_inventory_reservations_warehouse_id ON shop.inventory_reservations USING btree (warehouse_id)`.

**Trigger**

- `trg_inventory_reservation`: `CREATE TRIGGER trg_inventory_reservation BEFORE INSERT OR UPDATE ON shop.inventory_reservations FOR EACH ROW EXECUTE FUNCTION shop.validate_inventory_reservation()`.

### shop.stock_receipt_items

Các dòng SKU, số lượng và giá vốn của phiếu nhập.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `receipt_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến phiếu nhập kho; chỉ sửa dòng khi phiếu còn draft. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `unit_cost_vnd` | `bigint` | Không | — | Không khai báo | Giá vốn nhập một đơn vị, nguyên VND; giá mẫu là dữ liệu giả lập. |

**Ràng buộc trong CSDL**

- `stock_receipt_items_id_not_null`: `NOT NULL id`.
- `stock_receipt_items_pkey`: `PRIMARY KEY (id)`.
- `stock_receipt_items_quantity_check`: `CHECK (quantity > 0)`.
- `stock_receipt_items_quantity_not_null`: `NOT NULL quantity`.
- `stock_receipt_items_receipt_id_fkey`: `FOREIGN KEY (receipt_id) REFERENCES shop.stock_receipts(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `stock_receipt_items_receipt_id_not_null`: `NOT NULL receipt_id`.
- `stock_receipt_items_receipt_id_variant_id_key`: `UNIQUE (receipt_id, variant_id)`.
- `stock_receipt_items_unit_cost_vnd_check`: `CHECK (unit_cost_vnd >= 0)`.
- `stock_receipt_items_unit_cost_vnd_not_null`: `NOT NULL unit_cost_vnd`.
- `stock_receipt_items_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `stock_receipt_items_variant_id_not_null`: `NOT NULL variant_id`.

**Chỉ mục**

- `ix_fk_stock_receipt_items_variant_id`: `CREATE INDEX ix_fk_stock_receipt_items_variant_id ON shop.stock_receipt_items USING btree (variant_id)`.
- `stock_receipt_items_pkey`: `CREATE UNIQUE INDEX stock_receipt_items_pkey ON shop.stock_receipt_items USING btree (id)`.
- `stock_receipt_items_receipt_id_variant_id_key`: `CREATE UNIQUE INDEX stock_receipt_items_receipt_id_variant_id_key ON shop.stock_receipt_items USING btree (receipt_id, variant_id)`.

**Trigger**

- `trg_receipt_items`: `CREATE TRIGGER trg_receipt_items BEFORE INSERT OR DELETE OR UPDATE ON shop.stock_receipt_items FOR EACH ROW EXECUTE FUNCTION shop.protect_receipt_items()`.

### shop.stock_receipts

Phiếu nhập kho: kho nhận, nhà cung cấp, người ghi nhận và trạng thái ghi sổ.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `warehouse_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến kho xử lý tồn và giữ hàng của đơn. |
| `supplier_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến nhà cung cấp thực hoặc giả lập có cờ is_demo. |
| `received_by` | `bigint` | Có | FK | Không khai báo | Tài khoản admin/nhân viên ghi nhận phiếu nhập. |
| `status` | `character varying(20)` | Không | — | 'draft'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `received_at` | `timestamp with time zone` | Không | — | now() | Thời điểm nhận hàng, dùng nhóm báo cáo nhập kho theo tháng. |
| `note` | `text` | Có | — | Không khai báo | Thuộc tính note của bảng stock_receipts; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `is_demo` | `boolean` | Không | — | false | Đánh dấu bản ghi giả lập phục vụ đồ án, không phải giao dịch thực. |

**Ràng buộc trong CSDL**

- `stock_receipts_code_key`: `UNIQUE (code)`.
- `stock_receipts_code_not_null`: `NOT NULL code`.
- `stock_receipts_id_not_null`: `NOT NULL id`.
- `stock_receipts_id_warehouse_id_key`: `UNIQUE (id, warehouse_id)`.
- `stock_receipts_is_demo_not_null`: `NOT NULL is_demo`.
- `stock_receipts_pkey`: `PRIMARY KEY (id)`.
- `stock_receipts_received_at_not_null`: `NOT NULL received_at`.
- `stock_receipts_received_by_fkey`: `FOREIGN KEY (received_by) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `stock_receipts_status_check`: `CHECK (status::text = ANY (ARRAY['draft'::character varying, 'posted'::character varying, 'cancelled'::character varying]::text[]))`.
- `stock_receipts_status_not_null`: `NOT NULL status`.
- `stock_receipts_supplier_id_fkey`: `FOREIGN KEY (supplier_id) REFERENCES shop.suppliers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `stock_receipts_warehouse_id_fkey`: `FOREIGN KEY (warehouse_id) REFERENCES shop.warehouses(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `stock_receipts_warehouse_id_not_null`: `NOT NULL warehouse_id`.

**Chỉ mục**

- `ix_fk_stock_receipts_received_by`: `CREATE INDEX ix_fk_stock_receipts_received_by ON shop.stock_receipts USING btree (received_by)`.
- `ix_fk_stock_receipts_supplier_id`: `CREATE INDEX ix_fk_stock_receipts_supplier_id ON shop.stock_receipts USING btree (supplier_id)`.
- `ix_fk_stock_receipts_warehouse_id`: `CREATE INDEX ix_fk_stock_receipts_warehouse_id ON shop.stock_receipts USING btree (warehouse_id)`.
- `stock_receipts_code_key`: `CREATE UNIQUE INDEX stock_receipts_code_key ON shop.stock_receipts USING btree (code)`.
- `stock_receipts_id_warehouse_id_key`: `CREATE UNIQUE INDEX stock_receipts_id_warehouse_id_key ON shop.stock_receipts USING btree (id, warehouse_id)`.
- `stock_receipts_pkey`: `CREATE UNIQUE INDEX stock_receipts_pkey ON shop.stock_receipts USING btree (id)`.

### shop.suppliers

Thông tin nhà cung cấp phục vụ phiếu nhập.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `email` | `character varying(254)` | Có | — | Không khai báo | Địa chỉ email; users yêu cầu lowercase và trim trước khi ghi. |
| `phone` | `character varying(20)` | Có | — | Không khai báo | Số điện thoại dạng văn bản để giữ số 0 đầu hoặc mã quốc gia. |
| `address_line` | `text` | Có | — | Không khai báo | Số nhà, đường và địa chỉ chi tiết; không suy luận từ định danh hành chính. |
| `is_demo` | `boolean` | Không | — | false | Đánh dấu bản ghi giả lập phục vụ đồ án, không phải giao dịch thực. |

**Ràng buộc trong CSDL**

- `suppliers_code_key`: `UNIQUE (code)`.
- `suppliers_code_not_null`: `NOT NULL code`.
- `suppliers_id_not_null`: `NOT NULL id`.
- `suppliers_is_demo_not_null`: `NOT NULL is_demo`.
- `suppliers_name_not_null`: `NOT NULL name`.
- `suppliers_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `suppliers_code_key`: `CREATE UNIQUE INDEX suppliers_code_key ON shop.suppliers USING btree (code)`.
- `suppliers_pkey`: `CREATE UNIQUE INDEX suppliers_pkey ON shop.suppliers USING btree (id)`.

### shop.warehouses

Danh mục kho hàng và vị trí kho.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(40)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `address_line` | `text` | Có | — | Không khai báo | Số nhà, đường và địa chỉ chi tiết; không suy luận từ định danh hành chính. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng warehouses; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `warehouses_code_key`: `UNIQUE (code)`.
- `warehouses_code_not_null`: `NOT NULL code`.
- `warehouses_id_not_null`: `NOT NULL id`.
- `warehouses_is_active_not_null`: `NOT NULL is_active`.
- `warehouses_name_not_null`: `NOT NULL name`.
- `warehouses_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `warehouses_code_key`: `CREATE UNIQUE INDEX warehouses_code_key ON shop.warehouses USING btree (code)`.
- `warehouses_pkey`: `CREATE UNIQUE INDEX warehouses_pkey ON shop.warehouses USING btree (id)`.

## Giỏ hàng, đơn hàng và giao nhận

### shop.cart_items

SKU và số lượng trong giỏ; dòng giỏ được xóa theo giỏ theo ràng buộc đang khai báo.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `cart_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến giỏ hàng; dòng giỏ được khóa đồng bộ khi checkout. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `added_at` | `timestamp with time zone` | Không | — | now() | Thời điểm thêm SKU vào giỏ hàng. |

**Ràng buộc trong CSDL**

- `cart_items_added_at_not_null`: `NOT NULL added_at`.
- `cart_items_cart_id_fkey`: `FOREIGN KEY (cart_id) REFERENCES shop.carts(id) ON DELETE CASCADE`; ON DELETE CASCADE, ON UPDATE NO ACTION.
- `cart_items_cart_id_not_null`: `NOT NULL cart_id`.
- `cart_items_cart_id_variant_id_key`: `UNIQUE (cart_id, variant_id)`.
- `cart_items_id_not_null`: `NOT NULL id`.
- `cart_items_pkey`: `PRIMARY KEY (id)`.
- `cart_items_quantity_check`: `CHECK (quantity >= 1 AND quantity <= 100)`.
- `cart_items_quantity_not_null`: `NOT NULL quantity`.
- `cart_items_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `cart_items_variant_id_not_null`: `NOT NULL variant_id`.

**Chỉ mục**

- `cart_items_cart_id_variant_id_key`: `CREATE UNIQUE INDEX cart_items_cart_id_variant_id_key ON shop.cart_items USING btree (cart_id, variant_id)`.
- `cart_items_pkey`: `CREATE UNIQUE INDEX cart_items_pkey ON shop.cart_items USING btree (id)`.
- `ix_fk_cart_items_variant_id`: `CREATE INDEX ix_fk_cart_items_variant_id ON shop.cart_items USING btree (variant_id)`.

**Trigger**

- `trg_cart_parent`: `CREATE TRIGGER trg_cart_parent BEFORE INSERT OR DELETE OR UPDATE ON shop.cart_items FOR EACH ROW EXECUTE FUNCTION shop.lock_cart_parent()`.

### shop.carts

Giỏ hiện tại gắn với một người dùng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK, UK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `carts_id_not_null`: `NOT NULL id`.
- `carts_pkey`: `PRIMARY KEY (id)`.
- `carts_updated_at_not_null`: `NOT NULL updated_at`.
- `carts_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `carts_user_id_key`: `UNIQUE (user_id)`.
- `carts_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `carts_pkey`: `CREATE UNIQUE INDEX carts_pkey ON shop.carts USING btree (id)`.
- `carts_user_id_key`: `CREATE UNIQUE INDEX carts_user_id_key ON shop.carts USING btree (user_id)`.

**Trigger**

- `trg_touch_carts`: `CREATE TRIGGER trg_touch_carts BEFORE UPDATE ON shop.carts FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.order_addresses

Bản chụp địa chỉ giao hàng của đơn để thay đổi sổ địa chỉ không đổi lịch sử đơn.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `order_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `recipient_name` | `character varying(150)` | Không | — | Không khai báo | Tên người nhận hàng; ở đơn hàng đây là bản sao chốt. |
| `address_label` | `character varying(100)` | Có | — | Không khai báo | Nhãn địa chỉ được chốt theo đơn, không đổi khi khách sửa sổ địa chỉ. |
| `recipient_phone` | `character varying(20)` | Không | — | Không khai báo | Số điện thoại người nhận hàng, lưu dạng văn bản. |
| `address_line` | `text` | Không | — | Không khai báo | Số nhà, đường và địa chỉ chi tiết; không suy luận từ định danh hành chính. |
| `ward` | `character varying(120)` | Có | — | Không khai báo | Tên phường/xã tại thời điểm ghi địa chỉ, có thể NULL khi chưa có dữ liệu. |
| `district` | `character varying(120)` | Có | — | Không khai báo | Tên quận/huyện nếu địa chỉ nguồn sử dụng; tùy chọn để hỗ trợ dữ liệu hành chính cũ. |
| `province` | `character varying(120)` | Không | — | Không khai báo | Tên tỉnh hoặc thành phố theo địa chỉ nhận hàng. |
| `country_code` | `character(2)` | Không | — | 'VN'::bpchar | Mã quốc gia hai ký tự, mặc định VN. |
| `delivery_note` | `text` | Có | — | Không khai báo | Ghi chú giao hàng. |

**Ràng buộc trong CSDL**

- `order_addresses_address_line_not_null`: `NOT NULL address_line`.
- `order_addresses_country_code_not_null`: `NOT NULL country_code`.
- `order_addresses_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_addresses_order_id_not_null`: `NOT NULL order_id`.
- `order_addresses_pkey`: `PRIMARY KEY (order_id)`.
- `order_addresses_province_not_null`: `NOT NULL province`.
- `order_addresses_recipient_name_not_null`: `NOT NULL recipient_name`.
- `order_addresses_recipient_phone_not_null`: `NOT NULL recipient_phone`.

**Chỉ mục**

- `order_addresses_pkey`: `CREATE UNIQUE INDEX order_addresses_pkey ON shop.order_addresses USING btree (order_id)`.

### shop.order_discounts

Các khoản giảm giá áp dụng cho một đơn và mã giảm giá được chụp lại.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `kind` | `character varying(20)` | Không | — | Không khai báo | Loại dữ liệu/nghiệp vụ được giới hạn bằng CHECK của bảng. |
| `code_snapshot` | `character varying(60)` | Không | — | Không khai báo | Thuộc tính code_snapshot của bảng order_discounts; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `amount_vnd` | `bigint` | Không | — | Không khai báo | Số tiền nguyên đồng Việt Nam của nghiệp vụ. |

**Ràng buộc trong CSDL**

- `order_discounts_amount_vnd_check`: `CHECK (amount_vnd >= 0)`.
- `order_discounts_amount_vnd_not_null`: `NOT NULL amount_vnd`.
- `order_discounts_code_snapshot_not_null`: `NOT NULL code_snapshot`.
- `order_discounts_id_not_null`: `NOT NULL id`.
- `order_discounts_kind_check`: `CHECK (kind::text = ANY (ARRAY['voucher'::character varying, 'membership'::character varying]::text[]))`.
- `order_discounts_kind_not_null`: `NOT NULL kind`.
- `order_discounts_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_discounts_order_id_kind_key`: `UNIQUE (order_id, kind)`.
- `order_discounts_order_id_not_null`: `NOT NULL order_id`.
- `order_discounts_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `order_discounts_order_id_kind_key`: `CREATE UNIQUE INDEX order_discounts_order_id_kind_key ON shop.order_discounts USING btree (order_id, kind)`.
- `order_discounts_pkey`: `CREATE UNIQUE INDEX order_discounts_pkey ON shop.order_discounts USING btree (id)`.

### shop.order_items

Các món trong đơn với bản chụp SKU/tên/ảnh/giá và số lượng tại lúc đặt.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `product_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `sku_snapshot` | `character varying(100)` | Không | — | Không khai báo | Thuộc tính sku_snapshot của bảng order_items; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `product_name_snapshot` | `character varying(250)` | Không | — | Không khai báo | Thuộc tính product_name_snapshot của bảng order_items; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `variant_snapshot` | `character varying(250)` | Không | — | Không khai báo | Thuộc tính variant_snapshot của bảng order_items; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `image_url_snapshot` | `text` | Có | — | Không khai báo | Thuộc tính image_url_snapshot của bảng order_items; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `unit_price_vnd` | `bigint` | Không | — | Không khai báo | Đơn giá chốt tại thời điểm tạo đơn, không đọc lại giá hiện tại. |
| `line_total_vnd` | `bigint` | Có | — | ((quantity)::bigint * unit_price_vnd) | Cột sinh tự động bằng số lượng nhân đơn giá đã chốt. |
| `flash_slot_item_id` | `bigint` | Có | FK | Không khai báo | Suất Flash Sale áp dụng tại checkout; NULL nếu mua theo giá thường. |

**Ràng buộc trong CSDL**

- `order_items_flash_slot_item_id_variant_id_fkey`: `FOREIGN KEY (flash_slot_item_id, variant_id) REFERENCES shop.flash_slot_items(id, variant_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_items_id_not_null`: `NOT NULL id`.
- `order_items_id_order_id_key`: `UNIQUE (id, order_id)`.
- `order_items_id_product_id_key`: `UNIQUE (id, product_id)`.
- `order_items_id_variant_id_key`: `UNIQUE (id, variant_id)`.
- `order_items_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_items_order_id_not_null`: `NOT NULL order_id`.
- `order_items_order_id_variant_id_key`: `UNIQUE (order_id, variant_id)`.
- `order_items_pkey`: `PRIMARY KEY (id)`.
- `order_items_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_items_product_id_not_null`: `NOT NULL product_id`.
- `order_items_product_name_snapshot_not_null`: `NOT NULL product_name_snapshot`.
- `order_items_quantity_check`: `CHECK (quantity > 0)`.
- `order_items_quantity_not_null`: `NOT NULL quantity`.
- `order_items_sku_snapshot_not_null`: `NOT NULL sku_snapshot`.
- `order_items_unit_price_vnd_check`: `CHECK (unit_price_vnd >= 0)`.
- `order_items_unit_price_vnd_not_null`: `NOT NULL unit_price_vnd`.
- `order_items_variant_id_not_null`: `NOT NULL variant_id`.
- `order_items_variant_id_product_id_fkey`: `FOREIGN KEY (variant_id, product_id) REFERENCES shop.product_variants(id, product_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_items_variant_snapshot_not_null`: `NOT NULL variant_snapshot`.

**Chỉ mục**

- `ix_fk_order_items_flash_slot_item_id`: `CREATE INDEX ix_fk_order_items_flash_slot_item_id ON shop.order_items USING btree (flash_slot_item_id)`.
- `ix_fk_order_items_product_id`: `CREATE INDEX ix_fk_order_items_product_id ON shop.order_items USING btree (product_id)`.
- `ix_fk_order_items_variant_id`: `CREATE INDEX ix_fk_order_items_variant_id ON shop.order_items USING btree (variant_id)`.
- `order_items_id_order_id_key`: `CREATE UNIQUE INDEX order_items_id_order_id_key ON shop.order_items USING btree (id, order_id)`.
- `order_items_id_product_id_key`: `CREATE UNIQUE INDEX order_items_id_product_id_key ON shop.order_items USING btree (id, product_id)`.
- `order_items_id_variant_id_key`: `CREATE UNIQUE INDEX order_items_id_variant_id_key ON shop.order_items USING btree (id, variant_id)`.
- `order_items_order_id_variant_id_key`: `CREATE UNIQUE INDEX order_items_order_id_variant_id_key ON shop.order_items USING btree (order_id, variant_id)`.
- `order_items_pkey`: `CREATE UNIQUE INDEX order_items_pkey ON shop.order_items USING btree (id)`.

### shop.order_status_history

Lịch sử chuyển trạng thái đơn, người thay đổi, lý do và thời điểm.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `from_status` | `character varying(30)` | Có | — | Không khai báo | Trạng thái trước chuyển đổi; NULL ở sự kiện khởi tạo. |
| `to_status` | `character varying(30)` | Không | — | Không khai báo | Trạng thái sau chuyển đổi được ghi lịch sử. |
| `changed_by` | `bigint` | Có | FK | Không khai báo | Người thực hiện chuyển trạng thái; NULL nếu tác vụ hệ thống. |
| `reason` | `text` | Có | — | Không khai báo | Thuộc tính reason của bảng order_status_history; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `order_status_history_changed_by_fkey`: `FOREIGN KEY (changed_by) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_status_history_created_at_not_null`: `NOT NULL created_at`.
- `order_status_history_id_not_null`: `NOT NULL id`.
- `order_status_history_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `order_status_history_order_id_not_null`: `NOT NULL order_id`.
- `order_status_history_pkey`: `PRIMARY KEY (id)`.
- `order_status_history_to_status_not_null`: `NOT NULL to_status`.

**Chỉ mục**

- `ix_fk_order_status_history_changed_by`: `CREATE INDEX ix_fk_order_status_history_changed_by ON shop.order_status_history USING btree (changed_by)`.
- `ix_fk_order_status_history_order_id`: `CREATE INDEX ix_fk_order_status_history_order_id ON shop.order_status_history USING btree (order_id)`.
- `order_status_history_pkey`: `CREATE UNIQUE INDEX order_status_history_pkey ON shop.order_status_history USING btree (id)`.

### shop.orders

Đơn hàng: khách, kho, phương thức thanh toán, tổng tiền, trạng thái và các mốc xử lý.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_number` | `character varying(60)` | Không | UK | Không khai báo | Mã đơn hiển thị cho khách, độc lập với khóa nội bộ. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `idempotency_key` | `character varying(100)` | Không | — | Không khai báo | Khóa chống thực hiện trùng nghiệp vụ khi gửi lại yêu cầu. |
| `warehouse_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến kho xử lý tồn và giữ hàng của đơn. |
| `payment_method_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến phương thức; attempt và event phải cùng provider theo FK kép. |
| `status` | `character varying(30)` | Không | — | 'pending_payment'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `payment_status` | `character varying(30)` | Không | — | 'unpaid'::character varying | Trạng thái tiền của đơn; độc lập với trạng thái xử lý/giao hàng. |
| `requires_payment_review` | `boolean` | Không | — | false | Có tiền đến muộn hoặc thu trùng cần đối soát; không xóa trạng thái paid của khoản chính. |
| `subtotal_vnd` | `bigint` | Không | — | 0 | Tổng các dòng hàng trước chiết khấu và phí vận chuyển. |
| `discount_vnd` | `bigint` | Không | — | 0 | Tổng giảm giá đã chốt, không vượt giá trị hàng. |
| `shipping_fee_vnd` | `bigint` | Không | — | 0 | Phí vận chuyển được backend xác nhận, nguyên đồng Việt Nam. |
| `total_vnd` | `bigint` | Không | — | 0 | Số tiền phải trả bằng tiền hàng trừ giảm giá cộng phí vận chuyển. |
| `points_earned` | `integer` | Không | — | 0 | Điểm đã tính cho đơn sau khi giao và nhận tiền, dùng thu hồi khi hoàn. |
| `membership_policy_id` | `bigint` | Có | FK | Không khai báo | Phiên bản chính sách điểm chốt ở thời điểm tạo đơn. |
| `points_multiplier_bps` | `integer` | Không | — | 10000 | Hệ số tích điểm theo bps; 10000 là nhân một lần. |
| `expires_at` | `timestamp with time zone` | Có | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `placed_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo đơn, độc lập thời điểm thu tiền hoặc giao hàng. |
| `paid_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm backend xác nhận đã nhận tiền từ nguồn đáng tin. |
| `delivered_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm giao thành công, dùng xét hạng và báo cáo giao hàng. |
| `cancelled_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm hủy hoặc đóng đơn quá hạn. |
| `cancel_reason` | `text` | Có | — | Không khai báo | Lý do hủy/đóng đơn phục vụ hỗ trợ và đối soát. |
| `hidden_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm khách ẩn đơn khỏi lịch sử; không xóa chứng từ gốc. |
| `note` | `text` | Có | — | Không khai báo | Thuộc tính note của bảng orders; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `is_demo` | `boolean` | Không | — | false | Đánh dấu bản ghi giả lập phục vụ đồ án, không phải giao dịch thực. |
| `request_fingerprint` | `character(64)` | Có | — | Không khai báo | Hash thông tin request checkout, chống dùng lại khóa với địa chỉ/phương thức/tổng tiền khác. |

**Ràng buộc trong CSDL**

- `orders_check`: `CHECK (discount_vnd <= subtotal_vnd)`.
- `orders_check1`: `CHECK (total_vnd = (subtotal_vnd - discount_vnd + shipping_fee_vnd))`.
- `orders_discount_vnd_check`: `CHECK (discount_vnd >= 0)`.
- `orders_discount_vnd_not_null`: `NOT NULL discount_vnd`.
- `orders_id_not_null`: `NOT NULL id`.
- `orders_id_payment_method_id_key`: `UNIQUE (id, payment_method_id)`.
- `orders_id_user_id_key`: `UNIQUE (id, user_id)`.
- `orders_idempotency_key_not_null`: `NOT NULL idempotency_key`.
- `orders_is_demo_not_null`: `NOT NULL is_demo`.
- `orders_membership_policy_id_fkey`: `FOREIGN KEY (membership_policy_id) REFERENCES shop.membership_policies(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `orders_order_number_key`: `UNIQUE (order_number)`.
- `orders_order_number_not_null`: `NOT NULL order_number`.
- `orders_payment_method_id_fkey`: `FOREIGN KEY (payment_method_id) REFERENCES shop.payment_methods(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `orders_payment_method_id_not_null`: `NOT NULL payment_method_id`.
- `orders_payment_status_check`: `CHECK (payment_status::text = ANY (ARRAY['unpaid'::character varying, 'paid'::character varying, 'partially_refunded'::character varying, 'refunded'::character varying, 'manual_review'::character varying]::text[]))`.
- `orders_payment_status_not_null`: `NOT NULL payment_status`.
- `orders_pkey`: `PRIMARY KEY (id)`.
- `orders_placed_at_not_null`: `NOT NULL placed_at`.
- `orders_points_earned_check`: `CHECK (points_earned >= 0)`.
- `orders_points_earned_not_null`: `NOT NULL points_earned`.
- `orders_points_multiplier_bps_check`: `CHECK (points_multiplier_bps >= 0)`.
- `orders_points_multiplier_bps_not_null`: `NOT NULL points_multiplier_bps`.
- `orders_requires_payment_review_not_null`: `NOT NULL requires_payment_review`.
- `orders_shipping_fee_vnd_check`: `CHECK (shipping_fee_vnd >= 0)`.
- `orders_shipping_fee_vnd_not_null`: `NOT NULL shipping_fee_vnd`.
- `orders_status_check`: `CHECK (status::text = ANY (ARRAY['pending_payment'::character varying, 'confirmed'::character varying, 'processing'::character varying, 'shipped'::character varying, 'delivered'::character varying, 'cancelled'::character varying, 'expired'::character varying]::text[]))`.
- `orders_status_not_null`: `NOT NULL status`.
- `orders_subtotal_vnd_check`: `CHECK (subtotal_vnd >= 0)`.
- `orders_subtotal_vnd_not_null`: `NOT NULL subtotal_vnd`.
- `orders_total_vnd_check`: `CHECK (total_vnd >= 0)`.
- `orders_total_vnd_not_null`: `NOT NULL total_vnd`.
- `orders_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `orders_user_id_idempotency_key_key`: `UNIQUE (user_id, idempotency_key)`.
- `orders_user_id_not_null`: `NOT NULL user_id`.
- `orders_warehouse_id_fkey`: `FOREIGN KEY (warehouse_id) REFERENCES shop.warehouses(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `orders_warehouse_id_not_null`: `NOT NULL warehouse_id`.

**Chỉ mục**

- `ix_fk_orders_membership_policy_id`: `CREATE INDEX ix_fk_orders_membership_policy_id ON shop.orders USING btree (membership_policy_id)`.
- `ix_fk_orders_payment_method_id`: `CREATE INDEX ix_fk_orders_payment_method_id ON shop.orders USING btree (payment_method_id)`.
- `ix_fk_orders_warehouse_id`: `CREATE INDEX ix_fk_orders_warehouse_id ON shop.orders USING btree (warehouse_id)`.
- `ix_orders_admin`: `CREATE INDEX ix_orders_admin ON shop.orders USING btree (status, placed_at DESC)`.
- `ix_orders_expiry`: `CREATE INDEX ix_orders_expiry ON shop.orders USING btree (expires_at) WHERE ((status)::text = 'pending_payment'::text)`.
- `ix_orders_user_date`: `CREATE INDEX ix_orders_user_date ON shop.orders USING btree (user_id, placed_at DESC)`.
- `orders_id_payment_method_id_key`: `CREATE UNIQUE INDEX orders_id_payment_method_id_key ON shop.orders USING btree (id, payment_method_id)`.
- `orders_id_user_id_key`: `CREATE UNIQUE INDEX orders_id_user_id_key ON shop.orders USING btree (id, user_id)`.
- `orders_order_number_key`: `CREATE UNIQUE INDEX orders_order_number_key ON shop.orders USING btree (order_number)`.
- `orders_pkey`: `CREATE UNIQUE INDEX orders_pkey ON shop.orders USING btree (id)`.
- `orders_user_id_idempotency_key_key`: `CREATE UNIQUE INDEX orders_user_id_idempotency_key_key ON shop.orders USING btree (user_id, idempotency_key)`.

### shop.shipment_events

Các sự kiện trạng thái giao nhận của một vận đơn.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `shipment_id` | `bigint` | Không | FK | Không khai báo | Thuộc tính shipment_id của bảng shipment_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `status` | `character varying(30)` | Không | — | Không khai báo | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `description` | `text` | Có | — | Không khai báo | Nội dung mô tả; backend làm sạch HTML trước khi hiển thị nếu có. |
| `occurred_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm nghiệp vụ xảy ra ở nguồn; phân biệt thời điểm nhận sự kiện. |
| `provider_event_id` | `character varying(150)` | Có | — | Không khai báo | Mã sự kiện từ provider, dùng chống callback/sự kiện lặp. |

**Ràng buộc trong CSDL**

- `shipment_events_id_not_null`: `NOT NULL id`.
- `shipment_events_occurred_at_not_null`: `NOT NULL occurred_at`.
- `shipment_events_pkey`: `PRIMARY KEY (id)`.
- `shipment_events_shipment_id_fkey`: `FOREIGN KEY (shipment_id) REFERENCES shop.shipments(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `shipment_events_shipment_id_not_null`: `NOT NULL shipment_id`.
- `shipment_events_shipment_id_provider_event_id_key`: `UNIQUE (shipment_id, provider_event_id)`.
- `shipment_events_status_not_null`: `NOT NULL status`.

**Chỉ mục**

- `shipment_events_pkey`: `CREATE UNIQUE INDEX shipment_events_pkey ON shop.shipment_events USING btree (id)`.
- `shipment_events_shipment_id_provider_event_id_key`: `CREATE UNIQUE INDEX shipment_events_shipment_id_provider_event_id_key ON shop.shipment_events USING btree (shipment_id, provider_event_id)`.

### shop.shipments

Thông tin vận chuyển của đơn: đơn vị giao, mã vận đơn và các mốc giao hàng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `carrier_name` | `character varying(100)` | Không | — | Không khai báo | Tên đơn vị vận chuyển. |
| `tracking_number` | `character varying(100)` | Có | — | Không khai báo | Mã vận đơn do đơn vị vận chuyển cấp. |
| `status` | `character varying(30)` | Không | — | 'created'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `shipped_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính shipped_at của bảng shipments; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `delivered_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm giao thành công, dùng xét hạng và báo cáo giao hàng. |

**Ràng buộc trong CSDL**

- `shipments_carrier_name_not_null`: `NOT NULL carrier_name`.
- `shipments_carrier_name_tracking_number_key`: `UNIQUE (carrier_name, tracking_number)`.
- `shipments_id_not_null`: `NOT NULL id`.
- `shipments_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `shipments_order_id_not_null`: `NOT NULL order_id`.
- `shipments_pkey`: `PRIMARY KEY (id)`.
- `shipments_status_check`: `CHECK (status::text = ANY (ARRAY['created'::character varying, 'in_transit'::character varying, 'delivered'::character varying, 'failed'::character varying, 'returned'::character varying]::text[]))`.
- `shipments_status_not_null`: `NOT NULL status`.

**Chỉ mục**

- `ix_fk_shipments_order_id`: `CREATE INDEX ix_fk_shipments_order_id ON shop.shipments USING btree (order_id)`.
- `shipments_carrier_name_tracking_number_key`: `CREATE UNIQUE INDEX shipments_carrier_name_tracking_number_key ON shop.shipments USING btree (carrier_name, tracking_number)`.
- `shipments_pkey`: `CREATE UNIQUE INDEX shipments_pkey ON shop.shipments USING btree (id)`.

## Thanh toán, đổi trả và hoàn tiền

### shop.payment_attempts

Mỗi lần tạo thanh toán cho đơn, gồm mã nhà cung cấp, số tiền, QR/URL và hạn dùng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `payment_method_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến phương thức; attempt và event phải cùng provider theo FK kép. |
| `provider_request_id` | `character varying(150)` | Không | UK | Không khai báo | Mã yêu cầu gửi tới cổng thanh toán, duy nhất mỗi lần thử. |
| `provider_transaction_id` | `character varying(150)` | Có | — | Không khai báo | Mã giao dịch thực của provider, duy nhất trong cùng phương thức. |
| `amount_vnd` | `bigint` | Không | — | Không khai báo | Số tiền nguyên đồng Việt Nam của nghiệp vụ. |
| `status` | `character varying(30)` | Không | — | 'pending'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `qr_payload` | `text` | Có | — | Không khai báo | Nội dung QR được provider trả về; không tự xem QR là bằng chứng đã thu tiền. |
| `checkout_url` | `text` | Có | — | Không khai báo | URL thanh toán của provider đã xác minh hostname. |
| `expires_at` | `timestamp with time zone` | Có | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `succeeded_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính succeeded_at của bảng payment_attempts; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `payment_attempts_amount_vnd_check`: `CHECK (amount_vnd >= 0)`.
- `payment_attempts_amount_vnd_not_null`: `NOT NULL amount_vnd`.
- `payment_attempts_created_at_not_null`: `NOT NULL created_at`.
- `payment_attempts_id_not_null`: `NOT NULL id`.
- `payment_attempts_id_order_id_key`: `UNIQUE (id, order_id)`.
- `payment_attempts_id_payment_method_id_key`: `UNIQUE (id, payment_method_id)`.
- `payment_attempts_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_attempts_order_id_not_null`: `NOT NULL order_id`.
- `payment_attempts_order_id_payment_method_id_fkey`: `FOREIGN KEY (order_id, payment_method_id) REFERENCES shop.orders(id, payment_method_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_attempts_payment_method_id_fkey`: `FOREIGN KEY (payment_method_id) REFERENCES shop.payment_methods(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_attempts_payment_method_id_not_null`: `NOT NULL payment_method_id`.
- `payment_attempts_pkey`: `PRIMARY KEY (id)`.
- `payment_attempts_provider_request_id_key`: `UNIQUE (provider_request_id)`.
- `payment_attempts_provider_request_id_not_null`: `NOT NULL provider_request_id`.
- `payment_attempts_status_check`: `CHECK (status::text = ANY (ARRAY['pending'::character varying, 'succeeded'::character varying, 'failed'::character varying, 'expired'::character varying, 'cancelled'::character varying, 'manual_review'::character varying]::text[]))`.
- `payment_attempts_status_not_null`: `NOT NULL status`.

**Chỉ mục**

- `ix_fk_payment_attempts_order_id`: `CREATE INDEX ix_fk_payment_attempts_order_id ON shop.payment_attempts USING btree (order_id)`.
- `payment_attempts_id_order_id_key`: `CREATE UNIQUE INDEX payment_attempts_id_order_id_key ON shop.payment_attempts USING btree (id, order_id)`.
- `payment_attempts_id_payment_method_id_key`: `CREATE UNIQUE INDEX payment_attempts_id_payment_method_id_key ON shop.payment_attempts USING btree (id, payment_method_id)`.
- `payment_attempts_pkey`: `CREATE UNIQUE INDEX payment_attempts_pkey ON shop.payment_attempts USING btree (id)`.
- `payment_attempts_provider_request_id_key`: `CREATE UNIQUE INDEX payment_attempts_provider_request_id_key ON shop.payment_attempts USING btree (provider_request_id)`.
- `ux_payment_provider_transaction`: `CREATE UNIQUE INDEX ux_payment_provider_transaction ON shop.payment_attempts USING btree (payment_method_id, provider_transaction_id) WHERE (provider_transaction_id IS NOT NULL)`.

### shop.payment_events

Sự kiện từ nhà cung cấp thanh toán, payload và tình trạng đã xử lý.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `payment_method_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến phương thức; attempt và event phải cùng provider theo FK kép. |
| `provider_event_id` | `character varying(160)` | Không | — | Không khai báo | Mã sự kiện từ provider, dùng chống callback/sự kiện lặp. |
| `attempt_id` | `bigint` | Không | FK | Không khai báo | Phiên thanh toán nhận callback/sự kiện. |
| `event_type` | `character varying(40)` | Không | — | Không khai báo | Thuộc tính event_type của bảng payment_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `payload` | `jsonb` | Không | — | '{}'::jsonb | Dữ liệu JSON tối thiểu; loại bỏ bí mật và dữ liệu thẻ trước khi ghi. |
| `received_at` | `timestamp with time zone` | Không | — | now() | Thời điểm nhận hàng, dùng nhóm báo cáo nhập kho theo tháng. |
| `processed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm worker/callback đã hoàn tất xử lý. |
| `processing_note` | `text` | Có | — | Không khai báo | Thuộc tính processing_note của bảng payment_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `payment_events_attempt_id_fkey`: `FOREIGN KEY (attempt_id) REFERENCES shop.payment_attempts(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_events_attempt_id_not_null`: `NOT NULL attempt_id`.
- `payment_events_attempt_id_payment_method_id_fkey`: `FOREIGN KEY (attempt_id, payment_method_id) REFERENCES shop.payment_attempts(id, payment_method_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_events_event_type_not_null`: `NOT NULL event_type`.
- `payment_events_id_not_null`: `NOT NULL id`.
- `payment_events_payload_not_null`: `NOT NULL payload`.
- `payment_events_payment_method_id_fkey`: `FOREIGN KEY (payment_method_id) REFERENCES shop.payment_methods(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `payment_events_payment_method_id_not_null`: `NOT NULL payment_method_id`.
- `payment_events_payment_method_id_provider_event_id_key`: `UNIQUE (payment_method_id, provider_event_id)`.
- `payment_events_pkey`: `PRIMARY KEY (id)`.
- `payment_events_provider_event_id_not_null`: `NOT NULL provider_event_id`.
- `payment_events_received_at_not_null`: `NOT NULL received_at`.

**Chỉ mục**

- `ix_fk_payment_events_attempt_id`: `CREATE INDEX ix_fk_payment_events_attempt_id ON shop.payment_events USING btree (attempt_id)`.
- `payment_events_payment_method_id_provider_event_id_key`: `CREATE UNIQUE INDEX payment_events_payment_method_id_provider_event_id_key ON shop.payment_events USING btree (payment_method_id, provider_event_id)`.
- `payment_events_pkey`: `CREATE UNIQUE INDEX payment_events_pkey ON shop.payment_events USING btree (id)`.

### shop.payment_methods

Danh mục phương thức thanh toán và cờ thanh toán trực tuyến.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(30)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(100)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `is_online` | `boolean` | Không | — | Không khai báo | Phương thức cần thanh toán online/QR; false là trả tiền khi nhận hàng. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng payment_methods; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `payment_methods_code_key`: `UNIQUE (code)`.
- `payment_methods_code_not_null`: `NOT NULL code`.
- `payment_methods_id_not_null`: `NOT NULL id`.
- `payment_methods_is_active_not_null`: `NOT NULL is_active`.
- `payment_methods_is_online_not_null`: `NOT NULL is_online`.
- `payment_methods_name_not_null`: `NOT NULL name`.
- `payment_methods_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `payment_methods_code_key`: `CREATE UNIQUE INDEX payment_methods_code_key ON shop.payment_methods USING btree (code)`.
- `payment_methods_pkey`: `CREATE UNIQUE INDEX payment_methods_pkey ON shop.payment_methods USING btree (id)`.

### shop.refund_items

Các món được hoàn, số tiền/số lượng và cờ nhập lại kho.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `refund_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến nghiệp vụ hoàn tiền. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `order_item_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `amount_vnd` | `bigint` | Không | — | Không khai báo | Số tiền nguyên đồng Việt Nam của nghiệp vụ. |
| `restock` | `boolean` | Không | — | false | Chỉ true khi hàng trả đã được nhận và đủ điều kiện nhập lại kho. |

**Ràng buộc trong CSDL**

- `refund_items_amount_vnd_check`: `CHECK (amount_vnd >= 0)`.
- `refund_items_amount_vnd_not_null`: `NOT NULL amount_vnd`.
- `refund_items_order_id_not_null`: `NOT NULL order_id`.
- `refund_items_order_item_id_not_null`: `NOT NULL order_item_id`.
- `refund_items_order_item_id_order_id_fkey`: `FOREIGN KEY (order_item_id, order_id) REFERENCES shop.order_items(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refund_items_pkey`: `PRIMARY KEY (refund_id, order_item_id)`.
- `refund_items_quantity_check`: `CHECK (quantity > 0)`.
- `refund_items_quantity_not_null`: `NOT NULL quantity`.
- `refund_items_refund_id_not_null`: `NOT NULL refund_id`.
- `refund_items_refund_id_order_id_fkey`: `FOREIGN KEY (refund_id, order_id) REFERENCES shop.refunds(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refund_items_restock_not_null`: `NOT NULL restock`.

**Chỉ mục**

- `ix_fk_refund_items_order_item_id`: `CREATE INDEX ix_fk_refund_items_order_item_id ON shop.refund_items USING btree (order_item_id)`.
- `refund_items_pkey`: `CREATE UNIQUE INDEX refund_items_pkey ON shop.refund_items USING btree (refund_id, order_item_id)`.

### shop.refunds

Yêu cầu/kết quả hoàn tiền của đơn, lần thanh toán và yêu cầu đổi/trả liên quan.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `payment_attempt_id` | `bigint` | Không | FK | Không khai báo | Phiên thực nhận tiền được dùng để yêu cầu hoàn. |
| `idempotency_key` | `character varying(100)` | Không | UK | Không khai báo | Khóa chống thực hiện trùng nghiệp vụ khi gửi lại yêu cầu. |
| `amount_vnd` | `bigint` | Không | — | Không khai báo | Số tiền nguyên đồng Việt Nam của nghiệp vụ. |
| `status` | `character varying(30)` | Không | — | 'requested'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `reason` | `text` | Không | — | Không khai báo | Thuộc tính reason của bảng refunds; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `provider_refund_id` | `character varying(150)` | Có | — | Không khai báo | Mã hoàn tiền thực của provider, chỉ ghi sau khi kết quả được xác thực. |
| `requested_by` | `bigint` | Có | FK | Không khai báo | Tài khoản đề nghị hoàn tiền; duyệt và gọi provider thuộc backend/admin. |
| `requested_at` | `timestamp with time zone` | Không | — | now() | Thuộc tính requested_at của bảng refunds; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `completed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm hoàn tiền được xác nhận thành công. |
| `return_request_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến yêu cầu trả/đổi/hoàn, phải cùng đơn hàng. |

**Ràng buộc trong CSDL**

- `fk_refund_return_order`: `FOREIGN KEY (return_request_id, order_id) REFERENCES shop.return_requests(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refunds_amount_vnd_check`: `CHECK (amount_vnd > 0)`.
- `refunds_amount_vnd_not_null`: `NOT NULL amount_vnd`.
- `refunds_id_not_null`: `NOT NULL id`.
- `refunds_id_order_id_key`: `UNIQUE (id, order_id)`.
- `refunds_idempotency_key_key`: `UNIQUE (idempotency_key)`.
- `refunds_idempotency_key_not_null`: `NOT NULL idempotency_key`.
- `refunds_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refunds_order_id_not_null`: `NOT NULL order_id`.
- `refunds_payment_attempt_id_not_null`: `NOT NULL payment_attempt_id`.
- `refunds_payment_attempt_id_order_id_fkey`: `FOREIGN KEY (payment_attempt_id, order_id) REFERENCES shop.payment_attempts(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refunds_pkey`: `PRIMARY KEY (id)`.
- `refunds_reason_not_null`: `NOT NULL reason`.
- `refunds_requested_at_not_null`: `NOT NULL requested_at`.
- `refunds_requested_by_fkey`: `FOREIGN KEY (requested_by) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `refunds_status_check`: `CHECK (status::text = ANY (ARRAY['requested'::character varying, 'approved'::character varying, 'processing'::character varying, 'succeeded'::character varying, 'failed'::character varying, 'rejected'::character varying]::text[]))`.
- `refunds_status_not_null`: `NOT NULL status`.

**Chỉ mục**

- `ix_fk_refunds_order_id`: `CREATE INDEX ix_fk_refunds_order_id ON shop.refunds USING btree (order_id)`.
- `ix_fk_refunds_requested_by`: `CREATE INDEX ix_fk_refunds_requested_by ON shop.refunds USING btree (requested_by)`.
- `ix_fk_refunds_return_request_id`: `CREATE INDEX ix_fk_refunds_return_request_id ON shop.refunds USING btree (return_request_id)`.
- `refunds_id_order_id_key`: `CREATE UNIQUE INDEX refunds_id_order_id_key ON shop.refunds USING btree (id, order_id)`.
- `refunds_idempotency_key_key`: `CREATE UNIQUE INDEX refunds_idempotency_key_key ON shop.refunds USING btree (idempotency_key)`.
- `refunds_pkey`: `CREATE UNIQUE INDEX refunds_pkey ON shop.refunds USING btree (id)`.
- `ux_refund_provider`: `CREATE UNIQUE INDEX ux_refund_provider ON shop.refunds USING btree (payment_attempt_id, provider_refund_id) WHERE (provider_refund_id IS NOT NULL)`.

### shop.return_items

Các món, số lượng và số tiền yêu cầu thuộc một yêu cầu đổi/trả.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `return_request_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến yêu cầu trả/đổi/hoàn, phải cùng đơn hàng. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `order_item_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `condition_note` | `text` | Có | — | Không khai báo | Thuộc tính condition_note của bảng return_items; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `requested_amount_vnd` | `bigint` | Có | — | Không khai báo | Số tiền khách đề nghị hoàn cho dòng hàng; chưa phải số tiền đã duyệt. |

**Ràng buộc trong CSDL**

- `return_items_order_id_not_null`: `NOT NULL order_id`.
- `return_items_order_item_id_not_null`: `NOT NULL order_item_id`.
- `return_items_order_item_id_order_id_fkey`: `FOREIGN KEY (order_item_id, order_id) REFERENCES shop.order_items(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `return_items_pkey`: `PRIMARY KEY (return_request_id, order_item_id)`.
- `return_items_quantity_check`: `CHECK (quantity > 0)`.
- `return_items_quantity_not_null`: `NOT NULL quantity`.
- `return_items_requested_amount_vnd_check`: `CHECK (requested_amount_vnd >= 0)`.
- `return_items_return_request_id_not_null`: `NOT NULL return_request_id`.
- `return_items_return_request_id_order_id_fkey`: `FOREIGN KEY (return_request_id, order_id) REFERENCES shop.return_requests(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.

**Chỉ mục**

- `ix_fk_return_items_order_item_id`: `CREATE INDEX ix_fk_return_items_order_item_id ON shop.return_items USING btree (order_item_id)`.
- `return_items_pkey`: `CREATE UNIQUE INDEX return_items_pkey ON shop.return_items USING btree (return_request_id, order_item_id)`.

### shop.return_requests

Yêu cầu đổi/trả, loại xử lý, lý do, bằng chứng và bản chụp chính sách.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `order_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `type` | `character varying(20)` | Không | — | Không khai báo | Thuộc tính type của bảng return_requests; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `status` | `character varying(30)` | Không | — | 'requested'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `reason` | `text` | Không | — | Không khai báo | Thuộc tính reason của bảng return_requests; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `evidence_urls` | `jsonb` | Không | — | '[]'::jsonb | Danh sách URL bằng chứng trả hàng, nên dùng file private và URL ký có hạn. |
| `resolution_note` | `text` | Có | — | Không khai báo | Thuộc tính resolution_note của bảng return_requests; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `policy_snapshot` | `jsonb` | Không | — | '{}'::jsonb | Bản sao chính sách trả/đổi áp dụng khi lập yêu cầu để đối soát sau này. |
| `requested_at` | `timestamp with time zone` | Không | — | now() | Thuộc tính requested_at của bảng return_requests; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `resolved_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính resolved_at của bảng return_requests; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `return_requests_code_key`: `UNIQUE (code)`.
- `return_requests_code_not_null`: `NOT NULL code`.
- `return_requests_evidence_urls_check`: `CHECK (jsonb_typeof(evidence_urls) = 'array'::text)`.
- `return_requests_evidence_urls_not_null`: `NOT NULL evidence_urls`.
- `return_requests_id_not_null`: `NOT NULL id`.
- `return_requests_id_order_id_key`: `UNIQUE (id, order_id)`.
- `return_requests_order_id_not_null`: `NOT NULL order_id`.
- `return_requests_order_id_user_id_fkey`: `FOREIGN KEY (order_id, user_id) REFERENCES shop.orders(id, user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `return_requests_pkey`: `PRIMARY KEY (id)`.
- `return_requests_policy_snapshot_not_null`: `NOT NULL policy_snapshot`.
- `return_requests_reason_not_null`: `NOT NULL reason`.
- `return_requests_requested_at_not_null`: `NOT NULL requested_at`.
- `return_requests_status_check`: `CHECK (status::text = ANY (ARRAY['requested'::character varying, 'approved'::character varying, 'rejected'::character varying, 'awaiting_return'::character varying, 'received'::character varying, 'resolved'::character varying, 'cancelled'::character varying]::text[]))`.
- `return_requests_status_not_null`: `NOT NULL status`.
- `return_requests_type_check`: `CHECK (type::text = ANY (ARRAY['return_refund'::character varying, 'refund_only'::character varying, 'exchange'::character varying]::text[]))`.
- `return_requests_type_not_null`: `NOT NULL type`.
- `return_requests_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `return_requests_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_return_requests_order_id`: `CREATE INDEX ix_fk_return_requests_order_id ON shop.return_requests USING btree (order_id)`.
- `ix_fk_return_requests_user_id`: `CREATE INDEX ix_fk_return_requests_user_id ON shop.return_requests USING btree (user_id)`.
- `return_requests_code_key`: `CREATE UNIQUE INDEX return_requests_code_key ON shop.return_requests USING btree (code)`.
- `return_requests_id_order_id_key`: `CREATE UNIQUE INDEX return_requests_id_order_id_key ON shop.return_requests USING btree (id, order_id)`.
- `return_requests_pkey`: `CREATE UNIQUE INDEX return_requests_pkey ON shop.return_requests USING btree (id)`.

## Voucher và Flash Sale

### shop.flash_campaigns

Chương trình Flash Sale tổng, người tạo và trạng thái.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `status` | `character varying(20)` | Không | — | 'draft'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `created_by` | `bigint` | Có | FK | Không khai báo | Thuộc tính created_by của bảng flash_campaigns; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `flash_campaigns_code_key`: `UNIQUE (code)`.
- `flash_campaigns_code_not_null`: `NOT NULL code`.
- `flash_campaigns_created_at_not_null`: `NOT NULL created_at`.
- `flash_campaigns_created_by_fkey`: `FOREIGN KEY (created_by) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_campaigns_id_not_null`: `NOT NULL id`.
- `flash_campaigns_name_not_null`: `NOT NULL name`.
- `flash_campaigns_pkey`: `PRIMARY KEY (id)`.
- `flash_campaigns_status_check`: `CHECK (status::text = ANY (ARRAY['draft'::character varying, 'published'::character varying, 'cancelled'::character varying]::text[]))`.
- `flash_campaigns_status_not_null`: `NOT NULL status`.

**Chỉ mục**

- `flash_campaigns_code_key`: `CREATE UNIQUE INDEX flash_campaigns_code_key ON shop.flash_campaigns USING btree (code)`.
- `flash_campaigns_pkey`: `CREATE UNIQUE INDEX flash_campaigns_pkey ON shop.flash_campaigns USING btree (id)`.
- `ix_fk_flash_campaigns_created_by`: `CREATE INDEX ix_fk_flash_campaigns_created_by ON shop.flash_campaigns USING btree (created_by)`.

### shop.flash_reservations

Giữ quota Flash Sale cho món đơn hàng trước khi hoàn tất/hết hạn.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `order_item_id` | `bigint` | Không | FK, UK | Không khai báo | Khóa ngoại đến dòng hàng trong đơn. |
| `slot_item_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến suất SKU trong một khung giờ Flash Sale. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `quantity` | `integer` | Không | — | Không khai báo | Số lượng đơn vị sản phẩm, là số nguyên dương. |
| `status` | `character varying(20)` | Không | — | 'held'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `expires_at` | `timestamp with time zone` | Có | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `flash_reservations_created_at_not_null`: `NOT NULL created_at`.
- `flash_reservations_id_not_null`: `NOT NULL id`.
- `flash_reservations_order_item_id_fkey`: `FOREIGN KEY (order_item_id) REFERENCES shop.order_items(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_reservations_order_item_id_key`: `UNIQUE (order_item_id)`.
- `flash_reservations_order_item_id_not_null`: `NOT NULL order_item_id`.
- `flash_reservations_pkey`: `PRIMARY KEY (id)`.
- `flash_reservations_quantity_check`: `CHECK (quantity > 0)`.
- `flash_reservations_quantity_not_null`: `NOT NULL quantity`.
- `flash_reservations_slot_item_id_fkey`: `FOREIGN KEY (slot_item_id) REFERENCES shop.flash_slot_items(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_reservations_slot_item_id_not_null`: `NOT NULL slot_item_id`.
- `flash_reservations_status_check`: `CHECK (status::text = ANY (ARRAY['held'::character varying, 'consumed'::character varying, 'released'::character varying]::text[]))`.
- `flash_reservations_status_not_null`: `NOT NULL status`.
- `flash_reservations_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_reservations_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `flash_reservations_order_item_id_key`: `CREATE UNIQUE INDEX flash_reservations_order_item_id_key ON shop.flash_reservations USING btree (order_item_id)`.
- `flash_reservations_pkey`: `CREATE UNIQUE INDEX flash_reservations_pkey ON shop.flash_reservations USING btree (id)`.
- `ix_fk_flash_reservations_slot_item_id`: `CREATE INDEX ix_fk_flash_reservations_slot_item_id ON shop.flash_reservations USING btree (slot_item_id)`.
- `ix_fk_flash_reservations_user_id`: `CREATE INDEX ix_fk_flash_reservations_user_id ON shop.flash_reservations USING btree (user_id)`.

**Trigger**

- `trg_flash_reservation`: `CREATE TRIGGER trg_flash_reservation BEFORE INSERT OR UPDATE ON shop.flash_reservations FOR EACH ROW EXECUTE FUNCTION shop.validate_flash_reservation()`.

### shop.flash_slot_items

SKU tham gia khung Flash Sale, giá sale, quota và lượng giữ/đã bán.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `slot_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến khung giờ trong chiến dịch. |
| `variant_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến SKU cụ thể theo màu, RAM và dung lượng. |
| `sale_price_vnd` | `bigint` | Không | — | Không khai báo | Giá bán trong khung giờ, phải không cao hơn giá SKU thường tại checkout. |
| `quota` | `integer` | Không | — | Không khai báo | Tổng số lượng được bán theo giá Flash Sale của suất. |
| `reserved_qty` | `integer` | Không | — | 0 | Số lượng Flash Sale đang giữ cho đơn chưa chốt. |
| `sold_qty` | `integer` | Không | — | 0 | Số lượng Flash Sale đã được chốt. |
| `per_user_limit` | `integer` | Không | — | 1 | Giới hạn mỗi tài khoản trong phạm vi voucher hoặc suất Flash Sale. |

**Ràng buộc trong CSDL**

- `flash_slot_items_check`: `CHECK ((reserved_qty + sold_qty) <= quota)`.
- `flash_slot_items_id_not_null`: `NOT NULL id`.
- `flash_slot_items_id_variant_id_key`: `UNIQUE (id, variant_id)`.
- `flash_slot_items_per_user_limit_check`: `CHECK (per_user_limit > 0)`.
- `flash_slot_items_per_user_limit_not_null`: `NOT NULL per_user_limit`.
- `flash_slot_items_pkey`: `PRIMARY KEY (id)`.
- `flash_slot_items_quota_check`: `CHECK (quota > 0)`.
- `flash_slot_items_quota_not_null`: `NOT NULL quota`.
- `flash_slot_items_reserved_qty_check`: `CHECK (reserved_qty >= 0)`.
- `flash_slot_items_reserved_qty_not_null`: `NOT NULL reserved_qty`.
- `flash_slot_items_sale_price_vnd_check`: `CHECK (sale_price_vnd >= 0)`.
- `flash_slot_items_sale_price_vnd_not_null`: `NOT NULL sale_price_vnd`.
- `flash_slot_items_slot_id_fkey`: `FOREIGN KEY (slot_id) REFERENCES shop.flash_slots(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_slot_items_slot_id_not_null`: `NOT NULL slot_id`.
- `flash_slot_items_slot_id_variant_id_key`: `UNIQUE (slot_id, variant_id)`.
- `flash_slot_items_sold_qty_check`: `CHECK (sold_qty >= 0)`.
- `flash_slot_items_sold_qty_not_null`: `NOT NULL sold_qty`.
- `flash_slot_items_variant_id_fkey`: `FOREIGN KEY (variant_id) REFERENCES shop.product_variants(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_slot_items_variant_id_not_null`: `NOT NULL variant_id`.

**Chỉ mục**

- `flash_slot_items_id_variant_id_key`: `CREATE UNIQUE INDEX flash_slot_items_id_variant_id_key ON shop.flash_slot_items USING btree (id, variant_id)`.
- `flash_slot_items_pkey`: `CREATE UNIQUE INDEX flash_slot_items_pkey ON shop.flash_slot_items USING btree (id)`.
- `flash_slot_items_slot_id_variant_id_key`: `CREATE UNIQUE INDEX flash_slot_items_slot_id_variant_id_key ON shop.flash_slot_items USING btree (slot_id, variant_id)`.
- `ix_fk_flash_slot_items_variant_id`: `CREATE INDEX ix_fk_flash_slot_items_variant_id ON shop.flash_slot_items USING btree (variant_id)`.

### shop.flash_slots

Khung thời gian thuộc một chương trình Flash Sale.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `campaign_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến chiến dịch Flash Sale. |
| `starts_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm bắt đầu, tính cả thời điểm này. |
| `ends_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm kết thúc, không tính thời điểm này. |

**Ràng buộc trong CSDL**

- `flash_slots_campaign_id_fkey`: `FOREIGN KEY (campaign_id) REFERENCES shop.flash_campaigns(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_slots_campaign_id_not_null`: `NOT NULL campaign_id`.
- `flash_slots_check`: `CHECK (ends_at > starts_at)`.
- `flash_slots_ends_at_not_null`: `NOT NULL ends_at`.
- `flash_slots_id_not_null`: `NOT NULL id`.
- `flash_slots_pkey`: `PRIMARY KEY (id)`.
- `flash_slots_starts_at_not_null`: `NOT NULL starts_at`.

**Chỉ mục**

- `flash_slots_pkey`: `CREATE UNIQUE INDEX flash_slots_pkey ON shop.flash_slots USING btree (id)`.
- `ix_fk_flash_slots_campaign_id`: `CREATE INDEX ix_fk_flash_slots_campaign_id ON shop.flash_slots USING btree (campaign_id)`.

### shop.flash_subscriptions

Người dùng đăng ký nhận nhắc cho một mục Flash Sale.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `user_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `slot_item_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến suất SKU trong một khung giờ Flash Sale. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `notified_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm đã gửi nhắc mở bán; worker dùng để tránh nhắc lại. |

**Ràng buộc trong CSDL**

- `flash_subscriptions_created_at_not_null`: `NOT NULL created_at`.
- `flash_subscriptions_pkey`: `PRIMARY KEY (user_id, slot_item_id)`.
- `flash_subscriptions_slot_item_id_fkey`: `FOREIGN KEY (slot_item_id) REFERENCES shop.flash_slot_items(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_subscriptions_slot_item_id_not_null`: `NOT NULL slot_item_id`.
- `flash_subscriptions_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `flash_subscriptions_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `flash_subscriptions_pkey`: `CREATE UNIQUE INDEX flash_subscriptions_pkey ON shop.flash_subscriptions USING btree (user_id, slot_item_id)`.
- `ix_fk_flash_subscriptions_slot_item_id`: `CREATE INDEX ix_fk_flash_subscriptions_slot_item_id ON shop.flash_subscriptions USING btree (slot_item_id)`.

### shop.voucher_redemptions

Các lượt giữ/sử dụng voucher gắn với người dùng và đơn.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `voucher_id` | `bigint` | Không | FK | Không khai báo | Thuộc tính voucher_id của bảng voucher_redemptions; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `order_id` | `bigint` | Không | FK, UK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `discount_vnd` | `bigint` | Không | — | Không khai báo | Tổng giảm giá đã chốt, không vượt giá trị hàng. |
| `status` | `character varying(20)` | Không | — | 'reserved'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `voucher_redemptions_created_at_not_null`: `NOT NULL created_at`.
- `voucher_redemptions_discount_vnd_check`: `CHECK (discount_vnd >= 0)`.
- `voucher_redemptions_discount_vnd_not_null`: `NOT NULL discount_vnd`.
- `voucher_redemptions_id_not_null`: `NOT NULL id`.
- `voucher_redemptions_order_id_key`: `UNIQUE (order_id)`.
- `voucher_redemptions_order_id_not_null`: `NOT NULL order_id`.
- `voucher_redemptions_order_id_user_id_fkey`: `FOREIGN KEY (order_id, user_id) REFERENCES shop.orders(id, user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_redemptions_pkey`: `PRIMARY KEY (id)`.
- `voucher_redemptions_status_check`: `CHECK (status::text = ANY (ARRAY['reserved'::character varying, 'applied'::character varying, 'released'::character varying]::text[]))`.
- `voucher_redemptions_status_not_null`: `NOT NULL status`.
- `voucher_redemptions_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_redemptions_user_id_not_null`: `NOT NULL user_id`.
- `voucher_redemptions_voucher_id_fkey`: `FOREIGN KEY (voucher_id) REFERENCES shop.vouchers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_redemptions_voucher_id_not_null`: `NOT NULL voucher_id`.

**Chỉ mục**

- `ix_fk_voucher_redemptions_user_id`: `CREATE INDEX ix_fk_voucher_redemptions_user_id ON shop.voucher_redemptions USING btree (user_id)`.
- `ix_voucher_redemptions`: `CREATE INDEX ix_voucher_redemptions ON shop.voucher_redemptions USING btree (voucher_id, user_id, status)`.
- `voucher_redemptions_order_id_key`: `CREATE UNIQUE INDEX voucher_redemptions_order_id_key ON shop.voucher_redemptions USING btree (order_id)`.
- `voucher_redemptions_pkey`: `CREATE UNIQUE INDEX voucher_redemptions_pkey ON shop.voucher_redemptions USING btree (id)`.

### shop.voucher_scopes

Phạm vi áp dụng voucher theo sản phẩm, danh mục hoặc hạng thành viên.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `voucher_id` | `bigint` | Không | FK | Không khai báo | Thuộc tính voucher_id của bảng voucher_scopes; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `product_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến mẫu sản phẩm. |
| `category_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến danh mục chính hoặc phạm vi danh mục của ưu đãi. |
| `tier_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến hạng thành viên hiện tại hoặc phạm vi ưu đãi. |

**Ràng buộc trong CSDL**

- `voucher_scopes_category_id_fkey`: `FOREIGN KEY (category_id) REFERENCES shop.categories(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_scopes_check`: `CHECK (num_nonnulls(product_id, category_id, tier_id) = 1)`.
- `voucher_scopes_id_not_null`: `NOT NULL id`.
- `voucher_scopes_pkey`: `PRIMARY KEY (id)`.
- `voucher_scopes_product_id_fkey`: `FOREIGN KEY (product_id) REFERENCES shop.products(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_scopes_tier_id_fkey`: `FOREIGN KEY (tier_id) REFERENCES shop.membership_tiers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_scopes_voucher_id_fkey`: `FOREIGN KEY (voucher_id) REFERENCES shop.vouchers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `voucher_scopes_voucher_id_not_null`: `NOT NULL voucher_id`.

**Chỉ mục**

- `ix_fk_voucher_scopes_category_id`: `CREATE INDEX ix_fk_voucher_scopes_category_id ON shop.voucher_scopes USING btree (category_id)`.
- `ix_fk_voucher_scopes_product_id`: `CREATE INDEX ix_fk_voucher_scopes_product_id ON shop.voucher_scopes USING btree (product_id)`.
- `ix_fk_voucher_scopes_tier_id`: `CREATE INDEX ix_fk_voucher_scopes_tier_id ON shop.voucher_scopes USING btree (tier_id)`.
- `ux_voucher_scope_category`: `CREATE UNIQUE INDEX ux_voucher_scope_category ON shop.voucher_scopes USING btree (voucher_id, category_id) WHERE (category_id IS NOT NULL)`.
- `ux_voucher_scope_product`: `CREATE UNIQUE INDEX ux_voucher_scope_product ON shop.voucher_scopes USING btree (voucher_id, product_id) WHERE (product_id IS NOT NULL)`.
- `ux_voucher_scope_tier`: `CREATE UNIQUE INDEX ux_voucher_scope_tier ON shop.voucher_scopes USING btree (voucher_id, tier_id) WHERE (tier_id IS NOT NULL)`.
- `voucher_scopes_pkey`: `CREATE UNIQUE INDEX voucher_scopes_pkey ON shop.voucher_scopes USING btree (id)`.

### shop.vouchers

Định nghĩa mã giảm giá, công thức/giới hạn tiền, thời hạn và hạn mức sử dụng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `discount_type` | `character varying(20)` | Không | — | Không khai báo | fixed: giảm tiền cố định; percent: giảm tỷ lệ tính theo bps. |
| `discount_value` | `bigint` | Không | — | Không khai báo | fixed: số VND; percent: điểm cơ bản, 10000 bằng 100 phần trăm. |
| `max_discount_vnd` | `bigint` | Có | — | Không khai báo | Mức giảm tối đa; NULL khi không áp trần riêng ngoài tiền hàng đủ điều kiện. |
| `minimum_order_vnd` | `bigint` | Không | — | 0 | Giá trị tiền hàng tối thiểu trước giảm để áp dụng voucher. |
| `starts_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm bắt đầu, tính cả thời điểm này. |
| `ends_at` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm kết thúc, không tính thời điểm này. |
| `usage_limit` | `integer` | Không | — | Không khai báo | Tổng số lượt voucher được giữ hoặc áp dụng thành công tối đa. |
| `per_user_limit` | `integer` | Không | — | 1 | Giới hạn mỗi tài khoản trong phạm vi voucher hoặc suất Flash Sale. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng vouchers; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `allow_flash_sale` | `boolean` | Không | — | false | Có cho voucher áp vào đơn chứa Flash Sale hay không. |

**Ràng buộc trong CSDL**

- `vouchers_allow_flash_sale_not_null`: `NOT NULL allow_flash_sale`.
- `vouchers_check`: `CHECK (ends_at > starts_at)`.
- `vouchers_check1`: `CHECK (discount_type::text <> 'percent'::text OR discount_value <= 10000)`.
- `vouchers_code_check`: `CHECK (code::text = upper(TRIM(BOTH FROM code)))`.
- `vouchers_code_key`: `UNIQUE (code)`.
- `vouchers_code_not_null`: `NOT NULL code`.
- `vouchers_discount_type_check`: `CHECK (discount_type::text = ANY (ARRAY['fixed'::character varying, 'percent'::character varying]::text[]))`.
- `vouchers_discount_type_not_null`: `NOT NULL discount_type`.
- `vouchers_discount_value_check`: `CHECK (discount_value > 0)`.
- `vouchers_discount_value_not_null`: `NOT NULL discount_value`.
- `vouchers_ends_at_not_null`: `NOT NULL ends_at`.
- `vouchers_id_not_null`: `NOT NULL id`.
- `vouchers_is_active_not_null`: `NOT NULL is_active`.
- `vouchers_max_discount_vnd_check`: `CHECK (max_discount_vnd > 0)`.
- `vouchers_minimum_order_vnd_check`: `CHECK (minimum_order_vnd >= 0)`.
- `vouchers_minimum_order_vnd_not_null`: `NOT NULL minimum_order_vnd`.
- `vouchers_name_not_null`: `NOT NULL name`.
- `vouchers_per_user_limit_check`: `CHECK (per_user_limit > 0)`.
- `vouchers_per_user_limit_not_null`: `NOT NULL per_user_limit`.
- `vouchers_pkey`: `PRIMARY KEY (id)`.
- `vouchers_starts_at_not_null`: `NOT NULL starts_at`.
- `vouchers_usage_limit_check`: `CHECK (usage_limit > 0)`.
- `vouchers_usage_limit_not_null`: `NOT NULL usage_limit`.

**Chỉ mục**

- `vouchers_code_key`: `CREATE UNIQUE INDEX vouchers_code_key ON shop.vouchers USING btree (code)`.
- `vouchers_pkey`: `CREATE UNIQUE INDEX vouchers_pkey ON shop.vouchers USING btree (id)`.

## Thành viên và điểm thưởng

### shop.membership_accounts

Hạng hiện tại của mỗi người dùng và mốc đánh giá hạng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `user_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `tier_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến hạng thành viên hiện tại hoặc phạm vi ưu đãi. |
| `joined_at` | `timestamp with time zone` | Không | — | now() | Thuộc tính joined_at của bảng membership_accounts; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `tier_evaluated_at` | `timestamp with time zone` | Có | — | Không khai báo | Lần gần nhất xét lại hạng theo chi tiêu và cửa sổ chính sách. |

**Ràng buộc trong CSDL**

- `membership_accounts_joined_at_not_null`: `NOT NULL joined_at`.
- `membership_accounts_pkey`: `PRIMARY KEY (user_id)`.
- `membership_accounts_tier_id_fkey`: `FOREIGN KEY (tier_id) REFERENCES shop.membership_tiers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `membership_accounts_tier_id_not_null`: `NOT NULL tier_id`.
- `membership_accounts_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `membership_accounts_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_membership_accounts_tier_id`: `CREATE INDEX ix_fk_membership_accounts_tier_id ON shop.membership_accounts USING btree (tier_id)`.
- `membership_accounts_pkey`: `CREATE UNIQUE INDEX membership_accounts_pkey ON shop.membership_accounts USING btree (user_id)`.

### shop.membership_benefits

Danh mục quyền lợi thành viên.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `description` | `text` | Không | — | Không khai báo | Nội dung mô tả; backend làm sạch HTML trước khi hiển thị nếu có. |

**Ràng buộc trong CSDL**

- `membership_benefits_code_key`: `UNIQUE (code)`.
- `membership_benefits_code_not_null`: `NOT NULL code`.
- `membership_benefits_description_not_null`: `NOT NULL description`.
- `membership_benefits_id_not_null`: `NOT NULL id`.
- `membership_benefits_name_not_null`: `NOT NULL name`.
- `membership_benefits_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `membership_benefits_code_key`: `CREATE UNIQUE INDEX membership_benefits_code_key ON shop.membership_benefits USING btree (code)`.
- `membership_benefits_pkey`: `CREATE UNIQUE INDEX membership_benefits_pkey ON shop.membership_benefits USING btree (id)`.

### shop.membership_policies

Chính sách quy đổi tiền thành điểm, thời gian xét hạng/hết hạn điểm và hiệu lực.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(150)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `vnd_per_point` | `bigint` | Không | — | Không khai báo | Số VND chi tiêu hàng hóa ròng để nhận một điểm trước hệ số hạng. |
| `tier_window_months` | `integer` | Không | — | 12 | Cửa sổ tháng lùi để tính chi tiêu xét nâng hoặc hạ hạng. |
| `points_expiry_months` | `integer` | Không | — | 12 | Số tháng hiệu lực của điểm mới; worker hết hạn ghi dòng đối ứng. |
| `effective_from` | `timestamp with time zone` | Không | — | Không khai báo | Thời điểm bắt đầu hiệu lực phiên bản chính sách. |
| `effective_to` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm hết hiệu lực không bao gồm; NULL là chưa định ngày kết thúc. |

**Ràng buộc trong CSDL**

- `membership_policies_check`: `CHECK (effective_to IS NULL OR effective_to > effective_from)`.
- `membership_policies_code_key`: `UNIQUE (code)`.
- `membership_policies_code_not_null`: `NOT NULL code`.
- `membership_policies_effective_from_not_null`: `NOT NULL effective_from`.
- `membership_policies_id_not_null`: `NOT NULL id`.
- `membership_policies_name_not_null`: `NOT NULL name`.
- `membership_policies_pkey`: `PRIMARY KEY (id)`.
- `membership_policies_points_expiry_months_check`: `CHECK (points_expiry_months > 0)`.
- `membership_policies_points_expiry_months_not_null`: `NOT NULL points_expiry_months`.
- `membership_policies_tier_window_months_check`: `CHECK (tier_window_months > 0)`.
- `membership_policies_tier_window_months_not_null`: `NOT NULL tier_window_months`.
- `membership_policies_vnd_per_point_check`: `CHECK (vnd_per_point > 0)`.
- `membership_policies_vnd_per_point_not_null`: `NOT NULL vnd_per_point`.

**Chỉ mục**

- `membership_policies_code_key`: `CREATE UNIQUE INDEX membership_policies_code_key ON shop.membership_policies USING btree (code)`.
- `membership_policies_pkey`: `CREATE UNIQUE INDEX membership_policies_pkey ON shop.membership_policies USING btree (id)`.

### shop.membership_tier_history

Nhật ký thay hạng từ hạng cũ sang hạng mới cùng chi tiêu đủ điều kiện.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `from_tier_id` | `bigint` | Có | FK | Không khai báo | Thuộc tính from_tier_id của bảng membership_tier_history; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `to_tier_id` | `bigint` | Không | FK | Không khai báo | Thuộc tính to_tier_id của bảng membership_tier_history; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `qualifying_spend_vnd` | `bigint` | Không | — | Không khai báo | Chi tiêu ròng hợp lệ tại lần xét hạng được ghi lịch sử. |
| `reason` | `text` | Không | — | Không khai báo | Thuộc tính reason của bảng membership_tier_history; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `evaluated_at` | `timestamp with time zone` | Không | — | now() | Thuộc tính evaluated_at của bảng membership_tier_history; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `membership_tier_history_evaluated_at_not_null`: `NOT NULL evaluated_at`.
- `membership_tier_history_from_tier_id_fkey`: `FOREIGN KEY (from_tier_id) REFERENCES shop.membership_tiers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `membership_tier_history_id_not_null`: `NOT NULL id`.
- `membership_tier_history_pkey`: `PRIMARY KEY (id)`.
- `membership_tier_history_qualifying_spend_vnd_check`: `CHECK (qualifying_spend_vnd >= 0)`.
- `membership_tier_history_qualifying_spend_vnd_not_null`: `NOT NULL qualifying_spend_vnd`.
- `membership_tier_history_reason_not_null`: `NOT NULL reason`.
- `membership_tier_history_to_tier_id_fkey`: `FOREIGN KEY (to_tier_id) REFERENCES shop.membership_tiers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `membership_tier_history_to_tier_id_not_null`: `NOT NULL to_tier_id`.
- `membership_tier_history_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.membership_accounts(user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `membership_tier_history_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_membership_tier_history_from_tier_id`: `CREATE INDEX ix_fk_membership_tier_history_from_tier_id ON shop.membership_tier_history USING btree (from_tier_id)`.
- `ix_fk_membership_tier_history_to_tier_id`: `CREATE INDEX ix_fk_membership_tier_history_to_tier_id ON shop.membership_tier_history USING btree (to_tier_id)`.
- `ix_fk_membership_tier_history_user_id`: `CREATE INDEX ix_fk_membership_tier_history_user_id ON shop.membership_tier_history USING btree (user_id)`.
- `membership_tier_history_pkey`: `CREATE UNIQUE INDEX membership_tier_history_pkey ON shop.membership_tier_history USING btree (id)`.

### shop.membership_tiers

Các hạng thành viên, thứ tự hạng, ngưỡng chi tiêu và hệ số ưu đãi.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(40)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `name` | `character varying(100)` | Không | — | Không khai báo | Tên hiển thị tiếng Việt hoặc tên chính thức. |
| `rank` | `integer` | Không | UK | Không khai báo | Thứ tự hạng thành viên tăng dần, duy nhất giữa các hạng. |
| `minimum_spend_vnd` | `bigint` | Không | — | Không khai báo | Ngưỡng chi tiêu hợp lệ để đạt hạng, theo cửa sổ chính sách. |
| `discount_bps` | `integer` | Không | — | 0 | Tỷ lệ giảm theo điểm cơ bản, 100 bps bằng 1 phần trăm. |
| `points_multiplier_bps` | `integer` | Không | — | 10000 | Hệ số tích điểm theo bps; 10000 là nhân một lần. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng membership_tiers; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `membership_tiers_code_key`: `UNIQUE (code)`.
- `membership_tiers_code_not_null`: `NOT NULL code`.
- `membership_tiers_discount_bps_check`: `CHECK (discount_bps >= 0 AND discount_bps <= 10000)`.
- `membership_tiers_discount_bps_not_null`: `NOT NULL discount_bps`.
- `membership_tiers_id_not_null`: `NOT NULL id`.
- `membership_tiers_is_active_not_null`: `NOT NULL is_active`.
- `membership_tiers_minimum_spend_vnd_check`: `CHECK (minimum_spend_vnd >= 0)`.
- `membership_tiers_minimum_spend_vnd_not_null`: `NOT NULL minimum_spend_vnd`.
- `membership_tiers_name_not_null`: `NOT NULL name`.
- `membership_tiers_pkey`: `PRIMARY KEY (id)`.
- `membership_tiers_points_multiplier_bps_check`: `CHECK (points_multiplier_bps >= 0 AND points_multiplier_bps <= 50000)`.
- `membership_tiers_points_multiplier_bps_not_null`: `NOT NULL points_multiplier_bps`.
- `membership_tiers_rank_check`: `CHECK (rank >= 0)`.
- `membership_tiers_rank_key`: `UNIQUE (rank)`.
- `membership_tiers_rank_not_null`: `NOT NULL rank`.

**Chỉ mục**

- `membership_tiers_code_key`: `CREATE UNIQUE INDEX membership_tiers_code_key ON shop.membership_tiers USING btree (code)`.
- `membership_tiers_pkey`: `CREATE UNIQUE INDEX membership_tiers_pkey ON shop.membership_tiers USING btree (id)`.
- `membership_tiers_rank_key`: `CREATE UNIQUE INDEX membership_tiers_rank_key ON shop.membership_tiers USING btree (rank)`.

### shop.points_ledger

Sổ cái cộng/trừ điểm; gắn đơn/hoàn tiền, ngày hết hạn và khóa chống ghi trùng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `order_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `refund_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến nghiệp vụ hoàn tiền. |
| `points_delta` | `integer` | Không | — | Không khai báo | Biến động điểm có dấu; cộng khi mua, trừ khi hoàn/điều chỉnh/hết hạn. |
| `reason` | `character varying(30)` | Không | — | Không khai báo | Thuộc tính reason của bảng points_ledger; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `idempotency_key` | `character varying(160)` | Không | UK | Không khai báo | Khóa chống thực hiện trùng nghiệp vụ khi gửi lại yêu cầu. |
| `expires_at` | `timestamp with time zone` | Có | — | Không khai báo | Hạn hiệu lực theo đồng hồ máy chủ; NULL khi không áp dụng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `note` | `text` | Có | — | Không khai báo | Thuộc tính note của bảng points_ledger; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `points_ledger_check`: `CHECK ((reason::text <> 'purchase'::text OR order_id IS NOT NULL AND refund_id IS NULL AND points_delta > 0) AND (reason::text <> 'refund'::text OR order_id IS NOT NULL AND refund_id IS NOT NULL AND points_delta < 0) AND (reason::text <> 'expiry'::text OR points_delta < 0))`.
- `points_ledger_created_at_not_null`: `NOT NULL created_at`.
- `points_ledger_id_not_null`: `NOT NULL id`.
- `points_ledger_idempotency_key_key`: `UNIQUE (idempotency_key)`.
- `points_ledger_idempotency_key_not_null`: `NOT NULL idempotency_key`.
- `points_ledger_order_id_fkey`: `FOREIGN KEY (order_id) REFERENCES shop.orders(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `points_ledger_order_id_user_id_fkey`: `FOREIGN KEY (order_id, user_id) REFERENCES shop.orders(id, user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `points_ledger_pkey`: `PRIMARY KEY (id)`.
- `points_ledger_points_delta_check`: `CHECK (points_delta <> 0)`.
- `points_ledger_points_delta_not_null`: `NOT NULL points_delta`.
- `points_ledger_reason_check`: `CHECK (reason::text = ANY (ARRAY['purchase'::character varying, 'refund'::character varying, 'expiry'::character varying, 'adjustment'::character varying]::text[]))`.
- `points_ledger_reason_not_null`: `NOT NULL reason`.
- `points_ledger_refund_id_fkey`: `FOREIGN KEY (refund_id) REFERENCES shop.refunds(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `points_ledger_refund_id_order_id_fkey`: `FOREIGN KEY (refund_id, order_id) REFERENCES shop.refunds(id, order_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `points_ledger_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.membership_accounts(user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `points_ledger_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_points_ledger_order_id`: `CREATE INDEX ix_fk_points_ledger_order_id ON shop.points_ledger USING btree (order_id)`.
- `ix_fk_points_ledger_refund_id`: `CREATE INDEX ix_fk_points_ledger_refund_id ON shop.points_ledger USING btree (refund_id)`.
- `ix_fk_points_ledger_user_id`: `CREATE INDEX ix_fk_points_ledger_user_id ON shop.points_ledger USING btree (user_id)`.
- `points_ledger_idempotency_key_key`: `CREATE UNIQUE INDEX points_ledger_idempotency_key_key ON shop.points_ledger USING btree (idempotency_key)`.
- `points_ledger_pkey`: `CREATE UNIQUE INDEX points_ledger_pkey ON shop.points_ledger USING btree (id)`.

### shop.tier_benefits

Bảng nối hạng thành viên với quyền lợi và nội dung cụ thể.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `tier_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến hạng thành viên hiện tại hoặc phạm vi ưu đãi. |
| `benefit_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến quyền lợi thành viên được mô tả độc lập với hạng. |
| `detail` | `text` | Có | — | Không khai báo | Thuộc tính detail của bảng tier_benefits; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `tier_benefits_benefit_id_fkey`: `FOREIGN KEY (benefit_id) REFERENCES shop.membership_benefits(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `tier_benefits_benefit_id_not_null`: `NOT NULL benefit_id`.
- `tier_benefits_pkey`: `PRIMARY KEY (tier_id, benefit_id)`.
- `tier_benefits_tier_id_fkey`: `FOREIGN KEY (tier_id) REFERENCES shop.membership_tiers(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `tier_benefits_tier_id_not_null`: `NOT NULL tier_id`.

**Chỉ mục**

- `ix_fk_tier_benefits_benefit_id`: `CREATE INDEX ix_fk_tier_benefits_benefit_id ON shop.tier_benefits USING btree (benefit_id)`.
- `tier_benefits_pkey`: `CREATE UNIQUE INDEX tier_benefits_pkey ON shop.tier_benefits USING btree (tier_id, benefit_id)`.

## Thông báo, hỗ trợ và vận hành

### shop.audit_logs

Nhật ký thao tác: người thực hiện, đối tượng, dữ liệu trước/sau và địa chỉ IP.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `actor_user_id` | `bigint` | Có | FK | Không khai báo | Tài khoản tạo hành động audit; NULL đối với tác vụ hệ thống. |
| `action` | `character varying(100)` | Không | — | Không khai báo | Thuộc tính action của bảng audit_logs; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `entity_type` | `character varying(80)` | Không | — | Không khai báo | Thuộc tính entity_type của bảng audit_logs; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `entity_id` | `character varying(100)` | Có | — | Không khai báo | Thuộc tính entity_id của bảng audit_logs; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `before_data` | `jsonb` | Có | — | Không khai báo | Dữ liệu trước sửa đã whitelist, loại bỏ mật khẩu/token/bí mật. |
| `after_data` | `jsonb` | Có | — | Không khai báo | Dữ liệu sau sửa đã whitelist, loại bỏ mật khẩu/token/bí mật. |
| `ip_address` | `inet` | Có | — | Không khai báo | Thuộc tính ip_address của bảng audit_logs; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `audit_logs_action_not_null`: `NOT NULL action`.
- `audit_logs_actor_user_id_fkey`: `FOREIGN KEY (actor_user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `audit_logs_created_at_not_null`: `NOT NULL created_at`.
- `audit_logs_entity_type_not_null`: `NOT NULL entity_type`.
- `audit_logs_id_not_null`: `NOT NULL id`.
- `audit_logs_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `audit_logs_pkey`: `CREATE UNIQUE INDEX audit_logs_pkey ON shop.audit_logs USING btree (id)`.
- `ix_fk_audit_logs_actor_user_id`: `CREATE INDEX ix_fk_audit_logs_actor_user_id ON shop.audit_logs USING btree (actor_user_id)`.

### shop.device_tokens

Token thiết bị dùng gửi thông báo, nền tảng và trạng thái hoạt động.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `token` | `text` | Không | UK | Không khai báo | Push device token, chỉ cấp cho backend gửi thông báo; không phải access token. |
| `platform` | `character varying(20)` | Không | — | 'android'::character varying | Nền tảng thiết bị, mặc định android. |
| `is_active` | `boolean` | Không | — | true | Thuộc tính is_active của bảng device_tokens; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `device_tokens_id_not_null`: `NOT NULL id`.
- `device_tokens_is_active_not_null`: `NOT NULL is_active`.
- `device_tokens_pkey`: `PRIMARY KEY (id)`.
- `device_tokens_platform_check`: `CHECK (platform::text = ANY (ARRAY['android'::character varying, 'web'::character varying, 'ios'::character varying]::text[]))`.
- `device_tokens_platform_not_null`: `NOT NULL platform`.
- `device_tokens_token_key`: `UNIQUE (token)`.
- `device_tokens_token_not_null`: `NOT NULL token`.
- `device_tokens_updated_at_not_null`: `NOT NULL updated_at`.
- `device_tokens_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `device_tokens_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `device_tokens_pkey`: `CREATE UNIQUE INDEX device_tokens_pkey ON shop.device_tokens USING btree (id)`.
- `device_tokens_token_key`: `CREATE UNIQUE INDEX device_tokens_token_key ON shop.device_tokens USING btree (token)`.
- `ix_fk_device_tokens_user_id`: `CREATE INDEX ix_fk_device_tokens_user_id ON shop.device_tokens USING btree (user_id)`.

**Trigger**

- `trg_touch_device_tokens`: `CREATE TRIGGER trg_touch_device_tokens BEFORE UPDATE ON shop.device_tokens FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.notification_preferences

Tùy chọn nhận cập nhật đơn, nhắc Flash Sale và marketing của người dùng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `user_id` | `bigint` | Không | PK, FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `order_updates` | `boolean` | Không | — | true | Thuộc tính order_updates của bảng notification_preferences; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `flash_sale_reminders` | `boolean` | Không | — | true | Thuộc tính flash_sale_reminders của bảng notification_preferences; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `marketing` | `boolean` | Không | — | false | Đồng ý nhận tin tiếp thị; mặc định false. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |

**Ràng buộc trong CSDL**

- `notification_preferences_flash_sale_reminders_not_null`: `NOT NULL flash_sale_reminders`.
- `notification_preferences_marketing_not_null`: `NOT NULL marketing`.
- `notification_preferences_order_updates_not_null`: `NOT NULL order_updates`.
- `notification_preferences_pkey`: `PRIMARY KEY (user_id)`.
- `notification_preferences_updated_at_not_null`: `NOT NULL updated_at`.
- `notification_preferences_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `notification_preferences_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `notification_preferences_pkey`: `CREATE UNIQUE INDEX notification_preferences_pkey ON shop.notification_preferences USING btree (user_id)`.

**Trigger**

- `trg_touch_notification_preferences`: `CREATE TRIGGER trg_touch_notification_preferences BEFORE UPDATE ON shop.notification_preferences FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

### shop.notifications

Thông báo trong ứng dụng: tiêu đề/nội dung, dữ liệu điều hướng, thời điểm đọc và khóa chống trùng.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `type` | `character varying(40)` | Không | — | Không khai báo | Thuộc tính type của bảng notifications; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `title` | `character varying(200)` | Không | — | Không khai báo | Tiêu đề ngắn hiển thị của đánh giá hoặc thông báo. |
| `body` | `text` | Không | — | Không khai báo | Thuộc tính body của bảng notifications; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `data` | `jsonb` | Không | — | '{}'::jsonb | Dữ liệu bổ sung JSON cho ứng dụng, không chứa bí mật. |
| `read_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm người dùng đánh dấu đã đọc thông báo. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `dedup_key` | `character varying(160)` | Có | UK | Không khai báo | Khóa duy nhất chống tạo/gửi trùng cùng sự kiện nghiệp vụ. |

**Ràng buộc trong CSDL**

- `notifications_body_not_null`: `NOT NULL body`.
- `notifications_created_at_not_null`: `NOT NULL created_at`.
- `notifications_data_not_null`: `NOT NULL data`.
- `notifications_dedup_key_key`: `UNIQUE (dedup_key)`.
- `notifications_id_not_null`: `NOT NULL id`.
- `notifications_pkey`: `PRIMARY KEY (id)`.
- `notifications_title_not_null`: `NOT NULL title`.
- `notifications_type_not_null`: `NOT NULL type`.
- `notifications_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `notifications_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_notifications_user`: `CREATE INDEX ix_notifications_user ON shop.notifications USING btree (user_id, created_at DESC)`.
- `notifications_dedup_key_key`: `CREATE UNIQUE INDEX notifications_dedup_key_key ON shop.notifications USING btree (dedup_key)`.
- `notifications_pkey`: `CREATE UNIQUE INDEX notifications_pkey ON shop.notifications USING btree (id)`.

### shop.outbox_events

Sự kiện chờ xử lý theo mẫu outbox: payload, thời điểm sẵn sàng, số lần thử và lỗi gần nhất.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `event_type` | `character varying(100)` | Không | — | Không khai báo | Thuộc tính event_type của bảng outbox_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `aggregate_id` | `character varying(100)` | Không | — | Không khai báo | Thuộc tính aggregate_id của bảng outbox_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `payload` | `jsonb` | Không | — | Không khai báo | Dữ liệu JSON tối thiểu; loại bỏ bí mật và dữ liệu thẻ trước khi ghi. |
| `dedup_key` | `character varying(160)` | Không | UK | Không khai báo | Khóa duy nhất chống tạo/gửi trùng cùng sự kiện nghiệp vụ. |
| `available_at` | `timestamp with time zone` | Không | — | now() | Thời điểm sớm nhất worker được lấy sự kiện. |
| `processed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thời điểm worker/callback đã hoàn tất xử lý. |
| `attempts` | `integer` | Không | — | 0 | Số lần thử hoặc lần xử lý; giới hạn theo CHECK của bảng. |
| `last_error` | `text` | Có | — | Không khai báo | Thuộc tính last_error của bảng outbox_events; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `outbox_events_aggregate_id_not_null`: `NOT NULL aggregate_id`.
- `outbox_events_attempts_check`: `CHECK (attempts >= 0)`.
- `outbox_events_attempts_not_null`: `NOT NULL attempts`.
- `outbox_events_available_at_not_null`: `NOT NULL available_at`.
- `outbox_events_created_at_not_null`: `NOT NULL created_at`.
- `outbox_events_dedup_key_key`: `UNIQUE (dedup_key)`.
- `outbox_events_dedup_key_not_null`: `NOT NULL dedup_key`.
- `outbox_events_event_type_not_null`: `NOT NULL event_type`.
- `outbox_events_id_not_null`: `NOT NULL id`.
- `outbox_events_payload_not_null`: `NOT NULL payload`.
- `outbox_events_pkey`: `PRIMARY KEY (id)`.

**Chỉ mục**

- `ix_outbox_pending`: `CREATE INDEX ix_outbox_pending ON shop.outbox_events USING btree (available_at, id) WHERE (processed_at IS NULL)`.
- `outbox_events_dedup_key_key`: `CREATE UNIQUE INDEX outbox_events_dedup_key_key ON shop.outbox_events USING btree (dedup_key)`.
- `outbox_events_pkey`: `CREATE UNIQUE INDEX outbox_events_pkey ON shop.outbox_events USING btree (id)`.

### shop.search_history

Từ khóa và thời điểm tìm kiếm của từng tài khoản.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `keyword` | `character varying(200)` | Không | — | Không khai báo | Từ khóa tìm kiếm gần đây của chính người dùng, tối đa 200 ký tự. |
| `searched_at` | `timestamp with time zone` | Không | — | now() | Lần gần nhất tìm từ khóa; UPSERT cập nhật để hiển thị lịch sử gần đây. |

**Ràng buộc trong CSDL**

- `search_history_id_not_null`: `NOT NULL id`.
- `search_history_keyword_check`: `CHECK (length(TRIM(BOTH FROM keyword)) > 0)`.
- `search_history_keyword_not_null`: `NOT NULL keyword`.
- `search_history_pkey`: `PRIMARY KEY (id)`.
- `search_history_searched_at_not_null`: `NOT NULL searched_at`.
- `search_history_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `search_history_user_id_keyword_key`: `UNIQUE (user_id, keyword)`.
- `search_history_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_search_history_recent`: `CREATE INDEX ix_search_history_recent ON shop.search_history USING btree (user_id, searched_at DESC)`.
- `search_history_pkey`: `CREATE UNIQUE INDEX search_history_pkey ON shop.search_history USING btree (id)`.
- `search_history_user_id_keyword_key`: `CREATE UNIQUE INDEX search_history_user_id_keyword_key ON shop.search_history USING btree (user_id, keyword)`.

### shop.store_settings

Cấu hình cửa hàng dạng JSON; bản tài liệu chỉ đọc cấu trúc cột, không xuất giá trị cấu hình đang lưu.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `integer` | Không | PK | Không khai báo | Định danh bản ghi; xem PK và cột IDENTITY để biết cách sinh giá trị. |
| `data` | `jsonb` | Không | — | '{}'::jsonb | JSON cấu hình hoặc dữ liệu điều hướng theo bảng. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất. |

**Ràng buộc trong CSDL**

- `store_settings_data_not_null`: `NOT NULL data`.
- `store_settings_id_check`: `CHECK (id = 1)`.
- `store_settings_id_not_null`: `NOT NULL id`.
- `store_settings_pkey`: `PRIMARY KEY (id)`.
- `store_settings_updated_at_not_null`: `NOT NULL updated_at`.

**Chỉ mục**

- `store_settings_pkey`: `CREATE UNIQUE INDEX store_settings_pkey ON shop.store_settings USING btree (id)`.

### shop.support_messages

Tin nhắn trong phiếu hỗ trợ với người gửi và các URL đính kèm.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `ticket_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến phiếu hỗ trợ chứa hội thoại. |
| `sender_user_id` | `bigint` | Không | FK | Không khai báo | Người gửi phải là chủ phiếu hoặc có vai trò ADMIN/SUPPORT/STAFF. |
| `body` | `text` | Không | — | Không khai báo | Thuộc tính body của bảng support_messages; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `attachment_urls` | `jsonb` | Không | — | '[]'::jsonb | Danh sách URL đính kèm hỗ trợ; backend kiểm quyền xem và loại file. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |

**Ràng buộc trong CSDL**

- `support_messages_attachment_urls_check`: `CHECK (jsonb_typeof(attachment_urls) = 'array'::text)`.
- `support_messages_attachment_urls_not_null`: `NOT NULL attachment_urls`.
- `support_messages_body_not_null`: `NOT NULL body`.
- `support_messages_created_at_not_null`: `NOT NULL created_at`.
- `support_messages_id_not_null`: `NOT NULL id`.
- `support_messages_pkey`: `PRIMARY KEY (id)`.
- `support_messages_sender_user_id_fkey`: `FOREIGN KEY (sender_user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `support_messages_sender_user_id_not_null`: `NOT NULL sender_user_id`.
- `support_messages_ticket_id_fkey`: `FOREIGN KEY (ticket_id) REFERENCES shop.support_tickets(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `support_messages_ticket_id_not_null`: `NOT NULL ticket_id`.

**Chỉ mục**

- `ix_fk_support_messages_sender_user_id`: `CREATE INDEX ix_fk_support_messages_sender_user_id ON shop.support_messages USING btree (sender_user_id)`.
- `ix_support_messages_ticket`: `CREATE INDEX ix_support_messages_ticket ON shop.support_messages USING btree (ticket_id, created_at, id)`.
- `support_messages_pkey`: `CREATE UNIQUE INDEX support_messages_pkey ON shop.support_messages USING btree (id)`.

**Trigger**

- `trg_support_sender`: `CREATE TRIGGER trg_support_sender BEFORE INSERT OR UPDATE ON shop.support_messages FOR EACH ROW EXECUTE FUNCTION shop.validate_support_sender()`.

### shop.support_tickets

Phiếu hỗ trợ của khách, đơn liên quan, phân loại, trạng thái và quản trị viên phụ trách.

| Cột | Kiểu SQL | Cho NULL | Khóa | Mặc định/sinh tự động | Ý nghĩa |
| --- | --- | --- | --- | --- | --- |
| `id` | `bigint` | Không | PK | IDENTITY BY DEFAULT | Khóa chính tự tăng của bản ghi. |
| `code` | `character varying(60)` | Không | UK | Không khai báo | Mã nghiệp vụ duy nhất, ổn định khi nhập dữ liệu. |
| `user_id` | `bigint` | Không | FK | Không khai báo | Khóa ngoại đến người dùng sở hữu hoặc thực hiện nghiệp vụ. |
| `order_id` | `bigint` | Có | FK | Không khai báo | Khóa ngoại đến đơn hàng; giữ lại để đối soát lịch sử. |
| `subject` | `character varying(200)` | Không | — | Không khai báo | Tiêu đề phiếu hỗ trợ. |
| `category` | `character varying(40)` | Không | — | 'general'::character varying | Thuộc tính category của bảng support_tickets; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `status` | `character varying(20)` | Không | — | 'open'::character varying | Trạng thái hợp lệ được giới hạn bằng CHECK; xem quy trình trong tài liệu. |
| `assigned_admin_id` | `bigint` | Có | FK | Không khai báo | Thuộc tính assigned_admin_id của bảng support_tickets; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |
| `created_at` | `timestamp with time zone` | Không | — | now() | Thời điểm tạo có múi giờ; hiển thị theo giờ Việt Nam. |
| `updated_at` | `timestamp with time zone` | Không | — | now() | Thời điểm cập nhật gần nhất; trigger tự cập nhật. |
| `closed_at` | `timestamp with time zone` | Có | — | Không khai báo | Thuộc tính closed_at của bảng support_tickets; kiểu dữ liệu và ràng buộc xem định nghĩa bảng. |

**Ràng buộc trong CSDL**

- `support_tickets_assigned_admin_id_fkey`: `FOREIGN KEY (assigned_admin_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `support_tickets_category_not_null`: `NOT NULL category`.
- `support_tickets_code_key`: `UNIQUE (code)`.
- `support_tickets_code_not_null`: `NOT NULL code`.
- `support_tickets_created_at_not_null`: `NOT NULL created_at`.
- `support_tickets_id_not_null`: `NOT NULL id`.
- `support_tickets_order_id_user_id_fkey`: `FOREIGN KEY (order_id, user_id) REFERENCES shop.orders(id, user_id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `support_tickets_pkey`: `PRIMARY KEY (id)`.
- `support_tickets_status_check`: `CHECK (status::text = ANY (ARRAY['open'::character varying, 'in_progress'::character varying, 'resolved'::character varying, 'closed'::character varying]::text[]))`.
- `support_tickets_status_not_null`: `NOT NULL status`.
- `support_tickets_subject_not_null`: `NOT NULL subject`.
- `support_tickets_updated_at_not_null`: `NOT NULL updated_at`.
- `support_tickets_user_id_fkey`: `FOREIGN KEY (user_id) REFERENCES shop.users(id)`; ON DELETE NO ACTION, ON UPDATE NO ACTION.
- `support_tickets_user_id_not_null`: `NOT NULL user_id`.

**Chỉ mục**

- `ix_fk_support_tickets_assigned_admin_id`: `CREATE INDEX ix_fk_support_tickets_assigned_admin_id ON shop.support_tickets USING btree (assigned_admin_id)`.
- `ix_fk_support_tickets_order_id`: `CREATE INDEX ix_fk_support_tickets_order_id ON shop.support_tickets USING btree (order_id)`.
- `ix_fk_support_tickets_user_id`: `CREATE INDEX ix_fk_support_tickets_user_id ON shop.support_tickets USING btree (user_id)`.
- `support_tickets_code_key`: `CREATE UNIQUE INDEX support_tickets_code_key ON shop.support_tickets USING btree (code)`.
- `support_tickets_pkey`: `CREATE UNIQUE INDEX support_tickets_pkey ON shop.support_tickets USING btree (id)`.

**Trigger**

- `trg_touch_support_tickets`: `CREATE TRIGGER trg_touch_support_tickets BEFORE UPDATE ON shop.support_tickets FOR EACH ROW EXECUTE FUNCTION shop.touch_updated_at()`.

## Các view

### shop.v_cart_summary

Tổng hợp dữ liệu giỏ hàng để đọc nhanh.

```sql
 SELECT c.id AS cart_id,
    c.user_id,
    count(ci.id) AS distinct_sku_count,
    COALESCE(sum(ci.quantity), 0::bigint) AS unit_count,
    COALESCE(sum(ci.quantity::bigint * v.price_vnd), 0::numeric) AS estimated_subtotal_vnd
   FROM shop.carts c
     LEFT JOIN shop.cart_items ci ON ci.cart_id = c.id
     LEFT JOIN shop.product_variants v ON v.id = ci.variant_id
  GROUP BY c.id;
```

### shop.v_category_descendants

Mở rộng cây danh mục cha–con phục vụ lọc toàn bộ nhánh.

```sql
 WITH RECURSIVE tree(ancestor_id, category_id) AS (
         SELECT categories.id,
            categories.id
           FROM shop.categories
          WHERE categories.is_active
        UNION
         SELECT tree_1.ancestor_id,
            child.id
           FROM tree tree_1
             JOIN shop.categories child ON child.parent_id = tree_1.category_id
          WHERE child.is_active
        )
 SELECT ancestor_id,
    category_id
   FROM tree;
```

### shop.v_flash_sale_live

Dữ liệu Flash Sale dùng để xác định chương trình/khung/SKU đang có hiệu lực.

```sql
 SELECT fi.id,
    fi.slot_id,
    fi.variant_id,
    fi.sale_price_vnd,
    fi.quota,
    fi.reserved_qty,
    fi.sold_qty,
    fi.per_user_limit,
    fs.starts_at,
    fs.ends_at,
    fc.name AS campaign_name,
    fi.quota - fi.reserved_qty - fi.sold_qty AS remaining_qty,
    clock_timestamp() AS server_now
   FROM shop.flash_slot_items fi
     JOIN shop.flash_slots fs ON fs.id = fi.slot_id
     JOIN shop.flash_campaigns fc ON fc.id = fs.campaign_id
  WHERE fc.status::text = 'published'::text AND fs.starts_at <= clock_timestamp() AND clock_timestamp() < fs.ends_at;
```

### shop.v_inventory_available

Dữ liệu tồn có thể bán, tính từ tồn vật lý và phần giữ.

```sql
 SELECT warehouse_id,
    variant_id,
    on_hand,
    reserved,
    updated_at,
    on_hand - reserved AS available_qty
   FROM shop.inventory_balances b;
```

### shop.v_membership_summary

Tổng hợp thông tin thành viên/hạng/điểm theo SQL của view.

```sql
 SELECT a.user_id,
    a.tier_id,
    t.code AS tier_code,
    t.name AS tier_name,
    t.rank,
    t.discount_bps,
    COALESCE(( SELECT sum(p.points_delta) AS sum
           FROM shop.points_ledger p
          WHERE p.user_id = a.user_id), 0::bigint) AS points_balance,
    a.tier_evaluated_at
   FROM shop.membership_accounts a
     JOIN shop.membership_tiers t ON t.id = a.tier_id;
```

### shop.v_monthly_refunds

Tổng hợp hoàn tiền theo tháng.

```sql
 SELECT date_trunc('month'::text, (r.completed_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date AS month,
    sum(r.amount_vnd) AS refunded_vnd,
    sum(
        CASE
            WHEN a.status::text = 'succeeded'::text THEN r.amount_vnd
            ELSE 0::bigint
        END) AS order_refunded_vnd,
    sum(
        CASE
            WHEN a.status::text = 'manual_review'::text THEN r.amount_vnd
            ELSE 0::bigint
        END) AS unmatched_payment_refunded_vnd,
    sum(
        CASE
            WHEN a.status::text = 'succeeded'::text THEN COALESCE(( SELECT sum(ri.amount_vnd) AS sum
               FROM shop.refund_items ri
              WHERE ri.refund_id = r.id), 0::numeric)
            ELSE 0::numeric
        END) AS merchandise_refunded_vnd,
    count(*) AS refund_count
   FROM shop.refunds r
     JOIN shop.payment_attempts a ON a.id = r.payment_attempt_id
  WHERE r.status::text = 'succeeded'::text
  GROUP BY (date_trunc('month'::text, (r.completed_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date);
```

### shop.v_monthly_sales

Tổng hợp bán hàng theo tháng.

```sql
 SELECT date_trunc('month'::text, (delivered_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date AS month,
    sum(total_vnd) AS gross_collected_vnd,
    sum(subtotal_vnd - discount_vnd) AS merchandise_revenue_vnd,
    count(*) AS delivered_order_count,
    sum(( SELECT sum(i.quantity) AS sum
           FROM shop.order_items i
          WHERE i.order_id = o.id)) AS units_sold
   FROM shop.orders o
  WHERE status::text = 'delivered'::text AND (payment_status::text = ANY (ARRAY['paid'::character varying, 'partially_refunded'::character varying, 'refunded'::character varying]::text[]))
  GROUP BY (date_trunc('month'::text, (delivered_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date);
```

### shop.v_monthly_stock_receipts

Tổng hợp nhập kho theo tháng.

```sql
 SELECT date_trunc('month'::text, (r.received_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date AS month,
    sum(i.quantity) AS units_received,
    sum(i.quantity::bigint * i.unit_cost_vnd) AS purchase_cost_vnd
   FROM shop.stock_receipts r
     JOIN shop.stock_receipt_items i ON i.receipt_id = r.id
  WHERE r.status::text = 'posted'::text
  GROUP BY (date_trunc('month'::text, (r.received_at AT TIME ZONE 'Asia/Ho_Chi_Minh'::text))::date);
```

### shop.v_product_bestsellers

Tổng hợp số lượng bán theo sản phẩm phục vụ nhóm bán chạy.

```sql
 SELECT i.product_id,
    sum(i.quantity) AS units_sold
   FROM shop.order_items i
     JOIN shop.orders o ON o.id = i.order_id
  WHERE o.status::text = 'delivered'::text AND (o.payment_status::text = ANY (ARRAY['paid'::character varying, 'partially_refunded'::character varying, 'refunded'::character varying]::text[]))
  GROUP BY i.product_id;
```

### shop.v_product_catalog

Dữ liệu danh mục sản phẩm đã ghép theo SQL của view.

```sql
 SELECT p.id,
    p.code,
    p.name,
    p.slug,
    p.category_id,
    p.brand_id,
    p.is_featured,
    min(v.price_vnd) FILTER (WHERE v.status::text = 'active'::text) AS min_price_vnd,
    max(v.list_price_vnd) FILTER (WHERE v.status::text = 'active'::text) AS max_list_price_vnd,
    ( SELECT COALESCE(avg(r.rating), 0::numeric)::numeric(3,2) AS "coalesce"
           FROM shop.reviews r
          WHERE r.product_id = p.id AND r.status::text = 'published'::text) AS average_rating,
    ( SELECT count(*) AS count
           FROM shop.reviews r
          WHERE r.product_id = p.id AND r.status::text = 'published'::text) AS review_count,
    ( SELECT im.url
           FROM shop.product_images im
          WHERE im.product_id = p.id
          ORDER BY im.sort_order, im.id
         LIMIT 1) AS image_url
   FROM shop.products p
     LEFT JOIN shop.product_variants v ON v.product_id = p.id
  WHERE p.status::text = 'active'::text
  GROUP BY p.id;
```

### shop.v_product_new_arrivals

Phiếu nhập đã ghi sổ, trong 30 ngày gần nhất. Kể cả phiếu demo trong CSDL đồ án.

```sql
 SELECT p.id AS product_id,
    max(r.received_at) AS last_received_at
   FROM shop.stock_receipts r
     JOIN shop.stock_receipt_items i ON i.receipt_id = r.id
     JOIN shop.product_variants v ON v.id = i.variant_id AND v.status::text = 'active'::text
     JOIN shop.products p ON p.id = v.product_id AND p.status::text = 'active'::text
     JOIN shop.categories c ON c.id = p.category_id AND c.is_active
     JOIN shop.brands b ON b.id = p.brand_id AND b.is_active
  WHERE r.status::text = 'posted'::text AND r.received_at >= (now() - '30 days'::interval) AND r.received_at <= now()
  GROUP BY p.id;
```

### shop.v_search_trends

Báo cáo nội bộ, đếm số tài khoản đã tìm từ khóa trong 30 ngày.

```sql
 SELECT lower(btrim(keyword::text)) AS keyword,
    count(DISTINCT user_id) AS customer_count,
    max(searched_at) AS latest_search_at
   FROM shop.search_history
  WHERE searched_at >= (now() - '30 days'::interval)
  GROUP BY (lower(btrim(keyword::text)));
```

## Các hàm/thủ tục

Chữ ký và kiểu trả về được lấy trực tiếp từ Neon. Phần chức năng dùng comment của CSDL khi có, hoặc tên/chức năng SQL của đồ án.

| Hàm/chữ ký | Trả về | Chức năng |
| --- | --- | --- |
| `award_order_points(p_order_id bigint)` | `void` | Xử lý ghi điểm từ đơn theo quy tắc nghiệp vụ trong SQL. |
| `cancel_order(p_order_id bigint, p_user_id bigint, p_reason text)` | `void` | Xử lý hủy đơn và các dữ liệu giữ chỗ liên quan theo SQL. |
| `checkout(p_user_id bigint, p_address_id bigint, p_payment_method_code text, p_request_key text, p_voucher_code text, p_shipping_fee_vnd bigint)` | `bigint` | Chốt giỏ hàng, snapshot giá và địa chỉ, giữ tồn/suất/voucher bằng khóa hàng trong transaction. |
| `complete_refund(p_refund_id bigint, p_provider_refund_id text)` | `void` | Hoàn tất xử lý hoàn tiền và các điều chỉnh liên quan. |
| `expire_orders(p_limit integer)` | `integer` | Worker gọi mỗi phút để trả suất giữ hết hạn; giới hạn batch và SKIP LOCKED. |
| `lock_cart_parent()` | `trigger` | Khóa đối tượng giỏ cha khi thay đổi dữ liệu dòng giỏ. |
| `post_stock_receipt(p_receipt_id bigint)` | `void` | Ghi sổ phiếu nhập và cập nhật dữ liệu tồn/biến động kho. |
| `protect_receipt_items()` | `trigger` | Bảo vệ việc sửa dòng phiếu nhập theo trạng thái phiếu. |
| `record_payment_success(p_attempt_id bigint, p_event_id text, p_transaction_id text, p_amount_vnd bigint, p_payload jsonb)` | `text` | Ghi callback đã xác thực bởi backend; chống trùng và không hồi sinh đơn khi tiền đến muộn. |
| `refresh_membership(p_user_id bigint)` | `void` | Đánh giá/cập nhật thông tin hạng thành viên. |
| `release_order_reservations(p_order_id bigint)` | `void` | Giải phóng phần giữ tồn/quota của đơn theo nghiệp vụ. |
| `touch_updated_at()` | `trigger` | Đồng bộ cột updated_at khi bản ghi thay đổi. |
| `transition_order(p_order_id bigint, p_status text, p_actor_user_id bigint)` | `void` | Kiểm tra và ghi nhận chuyển trạng thái đơn. |
| `validate_flash_reservation()` | `trigger` | Kiểm tra các điều kiện của phần giữ quota Flash Sale. |
| `validate_inventory_reservation()` | `trigger` | Kiểm tra tính hợp lệ của giữ hàng theo món đơn/kho/SKU. |
| `validate_review_purchase()` | `trigger` | Kiểm tra điều kiện món đã mua khi ghi đánh giá. |
| `validate_support_sender()` | `trigger` | Kiểm tra người gửi được phép tham gia hội thoại hỗ trợ. |