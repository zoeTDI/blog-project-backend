package cn.caldm.www.permission_context.application.service;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

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

    @Override
    @Transactional
    public void grantResourceToRole(Long roleId, Long resourceId) {
        Role role = roleRepository.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在：" + roleId);
        }
        if (!role.isEnabled()) {
            throw new IllegalArgumentException("角色已停用，无法授权：" + roleId);
        }

        Resource resource = getResource(resourceId);
        if (!resource.isEnabled()) {
            throw new IllegalArgumentException("资源已停用，无法授权。");
        }

        if (roleResourceRelationRepository.exists(roleId, resourceId)) {
            throw new IllegalArgumentException("角色已拥有该资源权限。");
        }

        roleResourceRelationRepository.grant(roleId, resourceId);
    }

    @Override
    @Transactional
    public void revokeResourceFromRole(Long roleId, Long resourceId) {
        Role role = roleRepository.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在：" + roleId);
        }
        if (!role.isEnabled()) {
            throw new IllegalArgumentException("无法从停用的角色上移除权限：" + roleId);
        }

        Resource resource = getResource(resourceId);
        if (!resource.isEnabled()) {
            throw new IllegalArgumentException("无法从角色上移除停用的权限：" + resourceId);
        }

        if (!roleResourceRelationRepository.exists(roleId, resourceId)) {
            throw new IllegalArgumentException("角色尚未拥有该资源权限。");
        }
        roleResourceRelationRepository.revoke(roleId, resourceId);
    }

    private Resource getResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("资源不存在：" + id));
    }
}
