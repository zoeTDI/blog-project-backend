package cn.caldm.www.shared_kernel.security;

import java.util.List;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

/**
 *
 * 线程级安全上下文，用于在单次请求中传递用户信息
 *
 * @author caldm
 */
public class SecurityContextHolder {

    private record CurrentUser(Long userId, String username, List<RoleEnum> roles, List<String> menus) {
    }

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    public static Long getUserId() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程未绑定用户信息，请检查是否在 JwtFilter 有效范围内调用。");
        }
        return user.userId();
    }

    public static String getUsername() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程未绑定用户信息。");
        }
        return user.username();
    }

    public static List<RoleEnum> getRoles() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程未绑定用户信息。");
        }
        return user.roles();
    }

    public static List<String> getMenus() {
        CurrentUser user = CONTEXT.get();
        if (user == null) {
            throw new IllegalStateException("当前线程未绑定用户信息");
        }
        return user.menus();
    }

    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    public static class Manager {
        public static void setCurrentUser(Long userId, String username, List<RoleEnum> roles, List<String> menus) {
            if (userId == null || username == null || roles == null || roles.isEmpty() || menus == null
                    || menus.isEmpty()) {
                throw new IllegalArgumentException("userId 和 username 不能为空");
            }
            CONTEXT.set(new CurrentUser(userId, username, roles, menus));
        }

        public static void clear() {
            CONTEXT.remove();
        }
    }

}
