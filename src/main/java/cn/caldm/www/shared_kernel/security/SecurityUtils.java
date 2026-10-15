package cn.caldm.www.shared_kernel.security;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.user_context.domain.modal.UserRole;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @Nonnull
    public static List<UserRole> getRoles() {
        List<UserRole> roles = getCurrentUser().getRoles();
        return roles == null ? List.of() : List.copyOf(roles);
    }

    @Nonnull
    public static List<Permission> getPermissions() {
        List<Permission> permissions = getCurrentUser().getPermissions();
        return permissions == null ? List.of() : List.copyOf(permissions);
    }

    public static boolean isAuthenticated() {
        return getOptCurrentUser().isPresent();
    }
}
