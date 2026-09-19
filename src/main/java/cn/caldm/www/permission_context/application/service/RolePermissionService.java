package cn.caldm.www.permission_context.application.service;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.ResourceNode;

public interface RolePermissionService {
    List<ResourceNode> getResourceByRoleId(Long id);
}
