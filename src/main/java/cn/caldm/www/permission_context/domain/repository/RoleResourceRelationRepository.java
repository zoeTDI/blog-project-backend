package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

public interface RoleResourceRelationRepository {
    void grant(Long roleId, Long resourceId);

    void revoke(Long roleId, Long resourceId);

    List<Long> findResourceIdsByRoleId(Long roleId);

    List<Long> findResourceIdsByRoleIds(List<Long> roleIds);

    boolean exists(Long roleId, Long resourceId);
}
