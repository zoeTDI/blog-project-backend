package cn.caldm.www.permission_context.domain.repository;

import java.util.List;
import java.util.Map;

import cn.caldm.www.permission_context.domain.model.Role;

public interface RoleRepository {

    Role getRoleById(Long id);

    Role getRoleByCode(String code);

    List<Role> getRolesByCodes(List<String> codes);

    Map<String, Role> getRoleMap();

    void saveRole(Role role);

    void updateRole(Role newRole);

    void deleteRole(Long id);
}
