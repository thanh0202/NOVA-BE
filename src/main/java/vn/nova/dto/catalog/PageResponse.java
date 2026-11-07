package vn.nova.dto.catalog;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * DTO phân trang gồm items, total, page, size, totalPages và hasNext, giúp FE đọc danh sách nhất quán.
 * Các thành phần record là trường JSON; annotation validation kiểm tra đầu vào khi controller dùng @Valid.
 */
import java.util.List;

/** Trang đánh số từ 0; total là tổng kết quả của cùng bộ lọc, trước khi LIMIT/OFFSET. */
public record PageResponse<T>(List<T> items, long total, int page, int size,
                              long totalPages, boolean hasNext) {
    // Tạo DTO phân trang và tính totalPages/hasNext từ total, page, size.
    public static <T> PageResponse<T> of(List<T> items, long total, int page, int size) {
        long totalPages = (total + size - 1) / size;
        boolean hasNext = ((long) page + 1) * size < total;
        return new PageResponse<>(items, total, page, size, totalPages, hasNext);
    }
}
