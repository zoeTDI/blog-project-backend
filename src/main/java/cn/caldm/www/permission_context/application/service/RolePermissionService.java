package cn.caldm.www.permission_context.application.service;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.Role;

public interface RolePermissionService {

    /**
     * 查询指定角色可见的资源树。
     *
     * <p>合同：</p>
     * <ul>
     *   <li>id 为 null 时返回空列表；</li>
     *   <li>角色不存在时抛出 {@link IllegalArgumentException}；</li>
     *   <li>停用资源不出现在结果树中；</li>
     *   <li>返回形状为以根资源为顶点的资源树。</li>
     * </ul>
     *
     * @param id 角色 ID
     * @return 该角色可见的资源树；角色无任何资源时返回空列表
     */
    List<ResourceNode> getResourceByRoleId(Long id);

    void grantResourceToRole(Long roleId, Long resourceId);

    void revokeResourceFromRole(Long roleId, Long resourceId);

    /**
     * 查询全部角色。
     *
     * <p>合同：</p>
     * <ul>
     *   <li>返回全部角色（含停用角色），按显示顺序升序排列；</li>
     *   <li>无角色时返回空列表。</li>
     * </ul>
     *
     * @return 角色列表
     */
    List<Role> getAllRoles();
}
