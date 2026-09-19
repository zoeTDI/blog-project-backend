package cn.caldm.www.shared_kernel.security;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * 线程级安全上下文，用于在单次请求中传递用户信息
 *
 * @author caldm
 */
public class SecurityUtils {
    private SecurityUtils() {
    }

    public static Optional<AuthUser> getOptCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AuthUser authUser) {
            return Optional.of(authUser);
        }
        return Optional.empty();
    }

    public static AuthUser getCurrentUser() {
        return getOptCurrentUser().orElseThrow(() -> new IllegalStateException("The current thread is not bound to a valid user context."));
    }

    public static Long getUserId() {
        return getCurrentUser().getId();
    }

    public static String getUsername() {
        return getCurrentUser().getUsername();
    }

    public static List<RoleEnum> getRoles() {
        List<RoleEnum> roles = getCurrentUser().getRoles();
        return roles != null ? roles : Collections.emptyList();
    }

    public static boolean isAuthenticated() {
        return getOptCurrentUser().isPresent();
    }
}
