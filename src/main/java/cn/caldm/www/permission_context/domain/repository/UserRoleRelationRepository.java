package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.UserRoleRelation;

public interface UserRoleRelationRepository {

    List<UserRoleRelation> getRoleRelationByUserId(Long userId);

    void assignRoleToUser(Long userId, Long roleId);

    void assignRolesToUser(Long userId, List<Long> roleIds);

    void removeRoleFromUser(Long userId, Long roleId);

    void removeRolesFromUser(Long userId, List<Long> roleIds);
}
