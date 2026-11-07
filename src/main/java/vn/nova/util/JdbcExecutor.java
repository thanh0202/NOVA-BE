package vn.nova.util;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Lớp bọc JdbcTemplate để truy vấn danh sách/một dòng/giá trị số hoặc cập nhật với tham số ràng buộc.
 */
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import vn.nova.exception.ApiException;

/**
 * Các thao tác JDBC dùng chung cho Repository.
 * Tham số luôn truyền riêng qua dấu ?, không ghép dữ liệu client vào câu SQL.
 */
@Component
public class JdbcExecutor {
    private final JdbcTemplate jdbcTemplate;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public JdbcExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Chạy SQL có tham số và ánh xạ toàn bộ các dòng kết quả.
    public List<Map<String, Object>> findMany(String sql, Object... parameters) {
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            var metadata = resultSet.getMetaData();
            for (int column = 1; column <= metadata.getColumnCount(); column++) {
                Object value = resultSet.getObject(column);
                // Chuẩn hóa timestamp thành chuỗi ISO-8601 cho Android/web đọc giống nhau.
                if (value instanceof Timestamp timestamp) {
                    value = timestamp.toInstant().toString();
                } else if (value instanceof OffsetDateTime dateTime) {
                    value = dateTime.toInstant().toString();
                } else if (value instanceof java.sql.Date date) {
                    value = date.toLocalDate().toString();
                }
                row.put(metadata.getColumnLabel(column), value);
            }
            return row;
        }, parameters);
    }

    // Chạy truy vấn kỳ vọng tối đa một dòng, trả Optional khi chưa có dữ liệu.
    public Optional<Map<String, Object>> findOptional(String sql, Object... parameters) {
        return findMany(sql, parameters).stream().findFirst();
    }

    // Lấy một dòng bắt buộc và chuyển trường hợp không có thành lỗi phù hợp.
    public Map<String, Object> findOne(String sql, Object... parameters) {
        return findOptional(sql, parameters).orElseThrow(ApiException::notFound);
    }

    // Đọc một giá trị số nguyên dài từ truy vấn.
    public long findLong(String sql, Object... parameters) {
        return findOptional(sql, parameters)
                .map(row -> ((Number) row.values().iterator().next()).longValue())
                .orElse(0L);
    }

    // Chạy câu lệnh INSERT/UPDATE/DELETE với giá trị truyền bằng tham số JDBC.
    public int update(String sql, Object... parameters) {
        return jdbcTemplate.update(sql, parameters);
    }
}
