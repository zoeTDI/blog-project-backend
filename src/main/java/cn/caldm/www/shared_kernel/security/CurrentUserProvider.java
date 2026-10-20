package cn.caldm.www.shared_kernel.security;

import java.util.List;
import java.util.Optional;

/**
 * 当前用户信息端口
 * 由认证上下文提供实现；其余上下文只一来本接口
 */
public interface CurrentUserProvider {
    Optional<CurrentUser> getOptCurrentUser();

    default CurrentUser getCurrentUser() {
        return getOptCurrentUser().orElseThrow(
                () -> new IllegalStateException("The current thread is not bound to a valid user context."));
    }

    default Long getUserId() {
        return getCurrentUser().id();
    }

    default String getUsername() {
        return getCurrentUser().username();
    }

    default List<String> getRoles() {
        return getCurrentUser().roles();
    }

    default boolean isAuthenticated() {
        return getOptCurrentUser().isPresent();
    }
}
