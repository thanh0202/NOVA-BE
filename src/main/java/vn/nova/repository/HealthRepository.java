package vn.nova.repository;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Chạy truy vấn kiểm tra kết nối JDBC đến CSDL.
 * Đọc SQL cùng thứ tự tham số JDBC; các tên camelCase trong alias được dùng trong JSON trả về FE.
 */
import org.springframework.stereotype.Repository;
import vn.nova.util.JdbcExecutor;

/** Health kiểm tra một kết nối CSDL thật thay vì chỉ trả chuỗi UP cố định. */
@Repository
public class HealthRepository {
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public HealthRepository(JdbcExecutor database) {
        this.database = database;
    }

    // Chạy truy vấn nhỏ để kiểm tra JDBC/CSDL sẵn sàng.
    public void checkDatabase() {
        database.findLong("SELECT 1");
    }
}
