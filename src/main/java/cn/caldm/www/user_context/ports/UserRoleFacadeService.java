package cn.caldm.www.user_context.ports;

import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.user_context.domain.model.UserRole;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public interface UserRoleFacadeService {

    default boolean hasRole(Long userId, String roleCode) {
        if (userId == null || roleCode == null || roleCode.isBlank()) {
            return false;
        }
        return getUserRolesById(userId).stream().anyMatch(role -> roleCode.equals(role.getCode()));
    }

    /**
     * Retrieves the role codes assigned to the specified user.
     * @param userId the ID of the user
     * @return the role codes assigned to the user
     */
    List<UserRole> getUserRolesById(Long userId);

    /**
     * 批量查询多个用户的角色（一次查询，避免 N+1）。
     * @param userIds the IDs of the users
     * @return userId -> 角色列表 的映射；无任何关系的用户不出现在映射中
     */
    Map<Long, List<UserRole>> getUserRolesByUserIds(List<Long> userIds);

    /**
     * Assigns a role to the specified user.
     * @param userId the ID of the user
     * @param roleCode the code of the role to assign
     */
    void assignRoleToUser(Long userId, String roleCode);

    /**
     * Assigns multiple roles to the specified user.
     * @param userId the ID of the user
     * @param roleCodes the codes of the roles to assign
     */
    void assignRolesToUser(Long userId, List<String> roleCodes);

    /**
     * Removes a role from the specified user.
     * @param userId the ID of the user
     * @param roleCode the code of the role to remove
     */
    void removeRoleFromUser(Long userId, String roleCode);

    /**
     * Removes multiple roles from the specified user.
     * @param userId the ID of the user
     * @param roleCodes the codes of the roles to remove
     */
    void removeRolesFromUser(Long userId, List<String> roleCodes);

    List<UserRole> getRolesByIds(List<Long> roleIds);

    List<Permission> getPermissionByRoleIds(List<Long> roleIds);
}
