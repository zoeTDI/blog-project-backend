package cn.caldm.www.permission_context.application.service;

import java.util.List;

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

    private final RoleResourceRelationRepository roleResourceRelationRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public List<ResourceNode> getResourceByRoleId(Long id) {
        if (id == null) {
            return List.of();
        }
        List<Long> resourceIds = roleResourceRelationRepository.findResourceIdsByRoleId(id);
        if (resourceIds == null || resourceIds.isEmpty()) {
            return List.of();
        }
        List<Resource> resources = resourceRepository.findByIds(resourceIds);
        if (resources == null || resources.isEmpty()) {
            return List.of();
        }
        List<Resource> enabledResources = resources.stream().filter(res -> res.isEnabled()).toList();
        return ResourceUtils.buildTree(enabledResources);
    }

}
