package vn.nova.controller;

/*
 * NOVA — VAI TRÒ CỦA FILE
 * Các endpoint xem, lưu và xóa lịch sử tìm kiếm của người dùng đã đăng nhập.
 * @RequestMapping/@GetMapping/@PostMapping xác định URL; @RequestBody/@RequestParam/@PathVariable lấy dữ liệu HTTP.
 */
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import vn.nova.exception.ApiException;
import vn.nova.service.SessionService;
import vn.nova.util.JdbcExecutor;

/** Lịch sử riêng tư của tài khoản đăng nhập. */
@RestController
@RequestMapping("/api/search-history")
public class SearchHistoryController {
    private final SessionService sessions;
    private final JdbcExecutor database;

    // Nhận các dependency do Spring inject và lưu để dùng trong các phương thức nghiệp vụ.
    public SearchHistoryController(SessionService sessions, JdbcExecutor database) {
        this.sessions = sessions;
        this.database = database;
    }

    @GetMapping
    // Trả danh sách dữ liệu thuộc người dùng hoặc phạm vi được xác thực.
    public List<Map<String, Object>> list(@RequestHeader(value="Authorization",required=false) String token) {
        long user = sessions.requireUserId(token);
        return database.findMany("""
                SELECT id, keyword, keyword AS query, searched_at AS "createdAt"
                FROM shop.search_history WHERE user_id=? ORDER BY searched_at DESC,id DESC LIMIT 20
                """, user);
    }

    @PostMapping
    // Xác thực phiên, chuẩn hóa từ khóa và ghi lịch sử tìm kiếm của đúng userId.
    public Map<String, String> save(@RequestHeader(value="Authorization",required=false) String token,
                                    @RequestBody Map<String, Object> body) {
        long user = sessions.requireUserId(token);
        Object value = body.getOrDefault("keyword",body.get("query"));
        if (!(value instanceof String term) || term.isBlank() || term.strip().length() > 200) {
            throw ApiException.badRequest("Từ khóa phải có từ 1 đến 200 ký tự.");
        }
        database.update("""
                INSERT INTO shop.search_history(user_id,keyword) VALUES (?,?)
                ON CONFLICT(user_id,keyword) DO UPDATE SET searched_at=now()
                """, user, term.strip());
        return Map.of("message","Đã lưu tìm kiếm.");
    }

    @DeleteMapping
    // Xác thực phiên và xóa lịch sử tìm kiếm của chính tài khoản đó.
    public Map<String,String> clear(@RequestHeader(value="Authorization",required=false) String token) {
        database.update("DELETE FROM shop.search_history WHERE user_id=?",sessions.requireUserId(token));
        return Map.of("message","Đã xóa lịch sử.");
    }
}
