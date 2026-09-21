package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.Role;

public interface UserRoleRelationRepository {
    /**
     * Retrieves the roles assigned to the user with the specified user ID.
     * @param userId the user ID
     * @return a read-only list of roles
     */
    List<Role> getRolesByUserId(Long userId);

    void assignRoleToUser(Long userId, Long roleId);

    void assignRolesToUser(Long userId, List<Long> roleIds);

    void removeRoleFromUser(Long userId, Long roleId);

    void removeRolesFromUser(Long userId, List<Long> roleIds);

    List<Role> getRolesByCodes(List<String> codes);
}
