package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.domain.modal.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 *
 *
 *
 * @author caldm
 */
@Component("ss")
@RequiredArgsConstructor
public class SecurityExpressionService {

    private final CurrentUserProvider currentUserProvider;
    
    /**
     * 校验当前用户是否具备任意角色
     * @return 是否包含角色
     */
    public boolean hasRole() {
        return !currentUserProvider.getRoles().isEmpty();
    }

    /**
     * 校验当前用户是否具备指定角色（单个）
     *
     * @param roleCode 角色代码或枚举名，如 "ADMIN"
     * @return 是否包含该角色
     */
    public boolean hasRole(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            return false;
        }
        String normalized = roleCode.startsWith(AuthUser.ROLE_PREFIX)
                ? roleCode.substring(AuthUser.ROLE_PREFIX.length())
                : roleCode;
        return currentUserProvider.getRoles().contains(normalized);
    }

    /**
     * 支持直接传入 RoleEnum 枚举对象
     */
    public boolean hasRole(UserRole role) {
        if (role == null || role.getCode() == null) {
            return false;
        }
        return hasRole(role.getCode());
    }

    /**
     * 校验当前用户是否具备指定角色（多个，满足任意一个即可）
     *
     * @param roleCodes 角色代码列表
     * @return 是否包含任意一个角色
     */
    public boolean hasAnyRole(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return false;
        }
        return Arrays.stream(roleCodes).anyMatch(this::hasRole);
    }

    /**
     * 支持直接传入 UserRole 枚举数组（任意匹配一个）
     */
    public boolean hasAnyRole(UserRole... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        return Arrays.stream(roles).anyMatch(this::hasRole);
    }

    /**
     * 校验当前用户是否同时具备所有指定角色
     *
     * @param roleCodes 角色代码列表
     * @return 是否同时满足所有角色
     */
    public boolean hasAllRoles(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return false;
        }
        return Arrays.stream(roleCodes).allMatch(this::hasRole);
    }

    /**
     * 支持直接传入 RoleEnum 枚举数组（匹配全部）
     */
    public boolean hasAllRoles(UserRole... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        return Arrays.stream(roles).allMatch(this::hasRole);
    }

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return hasRole(new UserRole("ROLE_ADMIN"));
    }

    /**
     * 是否为审核员
     */
    public boolean isAuditor() {
        return hasRole(new UserRole("ROLE_AUDITOR"));
    }

    /**
     * 是否为作者
     */
    public boolean isAuthor() {
        return hasRole(new UserRole("ROLE_AUTHOR"));
    }

    /**
     * 是否具备管理权限
     * ADMIN 或 AUDITOR
     */
    public boolean isManager() {
        return hasAnyRole(
                new UserRole("ROLE_ADMIN"),
                new UserRole("ROLE_AUDITOR")
        );
    }

    /**
     * 是否可以管理文章内容
     * ADMIN 或 AUTHOR
     */
    public boolean isContentOperator() {
        return hasAnyRole(
                new UserRole("ROLE_ADMIN"),
                new UserRole("ROLE_AUTHOR")
        );
    }

    /**
     * 是否为内容相关角色
     */
    public boolean isContentUser() {
        return hasAnyRole(
                new UserRole("ROLE_AUTHOR"),
                new UserRole("ROLE_AUDITOR")
        );
    }
}
