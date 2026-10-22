package cn.caldm.www.shared_kernel.security;

import java.util.List;

/**
 * 当前登录用户的安全投影。
 *
 * <p>
 * 只暴露各调用方需要的字段；凭据、令牌等敏感信息
 * 保留在 {@code AuthUser} 内部，不通过本类型外泄。
 * </p>
 */
public record CurrentUser(Long id, String username, List<String> roles) {
    public CurrentUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
