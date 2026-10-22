package cn.caldm.www.user_context.application.service;

import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.user_context.domain.model.UserRole;

import java.util.List;

public interface UserRoleFacadeService {
    /**
     * Retrieves the role codes assigned to the specified user.
     * @param userId the ID of the user
     * @return the role codes assigned to the user
     */
    List<UserRole> getUserRolesById(Long userId);

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
