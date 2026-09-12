package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.shared_kernel.security.SecurityUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Component("ss")
public class SecurityExpressionService {
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
        RoleEnum targetRole = RoleEnum.fromCode(roleCode);
        if (targetRole == null) {
            return false;
        }
        List<RoleEnum> currentRoles = SecurityUtils.getRoles();
        return currentRoles.stream()
                .anyMatch(r -> r.equalsRole(targetRole));
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
}
