package cn.caldm.www.shared_kernel.security;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 线程级安全上下文，用于在单次请求中传递用户信息
 *
 * @author caldm
 */
public class SecurityContextHolder {
    private static final ThreadLocal<Map<String, Object>> CONTEXT_HOLDER = ThreadLocal.withInitial(HashMap::new);

    public static void setUserId(Long userId) {
        CONTEXT_HOLDER.get().put("userId", userId);
    }

    public static Long getUserId() {
        Object userId = CONTEXT_HOLDER.get().get("userId");
        return userId != null ? (Long) userId : 0L; // 默认返回 0L (表示：匿名用户）
    }

    public static void setUsername(String username) {
        CONTEXT_HOLDER.get().put("username", username);
    }

    public static String getUsername() {
        Object username = CONTEXT_HOLDER.get().get("username");
        return username != null ? (String) username : "anonymous";
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

}
