package cn.caldm.www.permission_context.application.service;

import java.util.List;

import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleResourceServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final RoleResourceRelationRepository roleResourceRelationRepository;
    private final ResourceRepository resourceRepository;

    @Override
    @Nonnull
    public List<ResourceNode> getResourceByRoleId(Long id) {
        if (id == null) {
            return List.of();
        }
        boolean existed = roleRepository.existsRole(id);
        if (!existed) {
            throw new IllegalArgumentException("角色不存在：" + id);
        }
        List<Long> resourceIds = roleResourceRelationRepository.findResourceIdsByRoleId(id);
        if (resourceIds == null || resourceIds.isEmpty()) {
            return List.of();
        }
        List<Resource> resources = resourceRepository.findByIds(resourceIds);
        if (resources == null || resources.isEmpty()) {
            return List.of();
        }
        List<Resource> enabledResources = resources.stream().filter(Resource::isEnabled).toList();
        return ResourceUtils.buildTree(enabledResources);
    }

}
