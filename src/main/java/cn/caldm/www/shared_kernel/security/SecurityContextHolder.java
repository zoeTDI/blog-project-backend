package cn.caldm.www.shared_kernel.security;

/**
 *
 * 线程级安全上下文，用于在单次请求中传递用户信息
 *
 * @author caldm
 */
public class SecurityContextHolder {

    private record CurrentUser(Long userId, String username) {}

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    public static Long getUserId() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程未绑定用户信息，请检查是否在 JwtFilter 有效范围内调用");
        }
        return user.userId();
    }

    public static String getUsername() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程为绑定用户信息");
        }
        return user.username();
    }

    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    public static class Manager {
        public static void setCurrentUser(Long userId, String username) {
            if (userId == null || username == null) {
                throw new IllegalArgumentException("userId 和 username 不能为空");
            }
            CONTEXT.set(new CurrentUser(userId, username));
        }

        public static void clear() {
            CONTEXT.remove();
        }
    }

}
