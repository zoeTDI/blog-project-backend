package cn.caldm.www.permission_context.application.service;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.ResourceNode;

public interface UserPermissionService {

    /** 用户可见菜单树：目录+菜单、仅启用、按 sort 升序（同值按 id）；ADMIN 角色返回全量 */
    List<ResourceNode> getUserMenuTree(Long userId);

    /** 用户按钮权限码列表：仅 BUTTON 类型、仅启用、码非空、升序；ADMIN 角色返回全量 */
    List<String> getUserPermissionCodes(Long userId);
}

