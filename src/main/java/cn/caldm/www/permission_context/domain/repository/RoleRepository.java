package cn.caldm.www.permission_context.domain.repository;

import java.util.Map;

import cn.caldm.www.permission_context.domain.model.Role;

public interface RoleRepository {

    Role getRoleById(Long id);

    Role getRoleByCode(String code);

    Map<String, Role> getRoleMap();

    void saveRole(Role role);

    void updateRole(Role newRole);

    void deleteRole(Long id);
}
