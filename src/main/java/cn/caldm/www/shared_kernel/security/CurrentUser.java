package cn.caldm.www.shared_kernel.security;

import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.user_context.domain.modal.UserRole;

import java.util.List;

/**
 * 当前登录用户的安全投影。
 *
 * <p>只暴露各调用方实际需要的字段；凭据、令牌等敏感信息
 * 保留在 {@code AuthUser} 内部，不通过本类型外泄。</p>
 */
public record CurrentUser(Long id, String username, List<UserRole> roles, List<Permission> permissions) {

    public CurrentUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
        permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }
}
