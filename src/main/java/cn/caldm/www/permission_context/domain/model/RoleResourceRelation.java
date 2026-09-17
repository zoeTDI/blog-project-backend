package cn.caldm.www.permission_context.domain.model;

import lombok.Getter;

/**
 *
 *
 *
 * @author caldm
 */
@Getter
public class RoleResourceRelation {
    private final Long roleId;
    private final Long resourceId;

    private RoleResourceRelation(Long roleId, Long resourceId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色 ID 不能为空。");
        }
        if (resourceId == null) {
            throw new IllegalArgumentException("资源 ID 不能为空。");
        }
        this.roleId = roleId;
        this.resourceId = resourceId;
    }

    public static RoleResourceRelation create(Long roleId, Long resourceId) {
        return new RoleResourceRelation(roleId, resourceId);
    }
}
