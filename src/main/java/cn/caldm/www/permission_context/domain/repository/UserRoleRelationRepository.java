package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.UserRoleRelation;

public interface UserRoleRelationRepository {

    List<UserRoleRelation> getRoleRelationByUserId(Long userId);

    /**
     * 批量查询多个用户的角色关系。
     *
     * @param userIds 用户 ID 列表
     * @return 关系列表；无数据时返回空列表
     */
    List<UserRoleRelation> getRoleRelationsByUserIds(List<Long> userIds);

    void assignRoleToUser(Long userId, Long roleId);

    void assignRolesToUser(Long userId, List<Long> roleIds);

    void removeRoleFromUser(Long userId, Long roleId);

    void removeRolesFromUser(Long userId, List<Long> roleIds);
}
