package cn.caldm.www.permission_context.domain.repository;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.Role;

public interface RoleRepository {

    Role getRoleById(Long id);

    List<Role> getRolesByIds(List<Long> ids);

    Role getRoleByCode(String code);

    List<Role> getRolesByCodes(List<String> codes);

    boolean existsRole(Long roleId);

    /**
     * 查询全部角色（按显示顺序升序）。
     *
     * @return 角色列表；无数据时返回空列表
     */
    List<Role> findAll();

}
