package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

public interface RoleResourceRepository {
    void grant(Long roleId, Long resourceId);

    void revoke(Long roleId, Long resourceId);

    void revokeAllByResourceId(Long resourceId);

    void revokeAllByRoleId(Long roleId);

    List<Long> findResourceIdsByRoleId(Long roleId);

    List<Long> findRoleIdsByResourceId(Long resourceId);

    boolean exists(Long roleId, Long resourceId);
}
