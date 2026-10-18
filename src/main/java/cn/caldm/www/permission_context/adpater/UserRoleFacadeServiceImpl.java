package cn.caldm.www.permission_context.adpater;

import java.util.List;
import java.util.Objects;

import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.user_context.domain.modal.UserRole;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import cn.caldm.www.user_context.application.service.UserRoleFacadeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleFacadeServiceImpl implements UserRoleFacadeService {

    private final UserRoleRelationRepository userRoleRelationRepository;
    private final RoleRepository roleRepository;
    private final RoleResourceRelationRepository roleResourceRelationRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public List<UserRole> getUserRolesById(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<UserRoleRelation> userRoleRelations = userRoleRelationRepository.getRoleRelationByUserId(userId);
        List<Long> roleIds = userRoleRelations.stream()
                .map(UserRoleRelation::getRoleId)
                .toList();
        List<Role> roles = roleRepository.getRolesByIds(roleIds);
        return roles.stream()
                .map(role -> new UserRole(role.getId(), role.getCode()))
                .toList();
    }

    @Override
    public void assignRoleToUser(Long userId, String roleCode) {
        Role role = roleRepository.getRoleByCode(roleCode);
        if (!role.isEnabled()) {
            return;
        }
        userRoleRelationRepository.assignRoleToUser(userId, role.getId());
    }

    @Override
    public void assignRolesToUser(Long userId, List<String> roleCodes) {
        Assert.notNull(roleCodes, "The list of role code must not be null");
        Assert.notEmpty(roleCodes, "The list of role code must contain element");

        List<String> filtered = roleCodes.stream().filter(Objects::nonNull).toList();
        List<Role> roles = roleRepository.getRolesByCodes(filtered);
        List<Role> enabledRoles = roles.stream().filter(Role::isEnabled).toList();
        List<Long> roleIds = enabledRoles.stream().map(Role::getId).filter(Objects::nonNull).toList();

        userRoleRelationRepository.assignRolesToUser(userId, roleIds);

    }

    @Override
    public void removeRoleFromUser(Long userId, String roleCode) {
        Role role = roleRepository.getRoleByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("The role is null.(roleCode: " + roleCode + ")");
        }
        userRoleRelationRepository.removeRoleFromUser(userId, role.getId());

    }

    @Override
    public void removeRolesFromUser(Long userId, List<String> roleCodes) {
        Assert.notNull(roleCodes, "The list of role code must be not null.");
        Assert.notEmpty(roleCodes, "The list of role code must contain element.");
        List<Role> roles = roleRepository.getRolesByCodes(roleCodes);
        List<Long> ids = roles.stream().map(Role::getId).toList();
        userRoleRelationRepository.removeRolesFromUser(userId, ids);
    }

    @Override
    @Nonnull
    public List<UserRole> getRolesByIds(List<Long> roleIds) {
        Assert.notNull(roleIds, "The list of role id must be not null.");
        List<Long> filtered = roleIds.stream().filter(Objects::nonNull).toList();
        if (filtered.isEmpty()) {
            return List.of();
        }
        List<Role> roles = roleRepository.getRolesByIds(filtered);
        return roles.stream()
                .filter(Objects::nonNull)
                .map(role -> new UserRole(role.getId(), role.getCode()))
                .toList();
    }

    @Override
    @Nonnull
    public List<Permission> getPermissionByRoleIds(List<Long> roleIds) {
        Assert.notNull(roleIds, "The list of role id must be not null.");
        List<Long> filtered = roleIds.stream().filter(Objects::nonNull).toList();
        if (filtered.isEmpty()) {
            return List.of();
        }
        List<Long> relationIds = roleResourceRelationRepository.findResourceIdsByRoleIds(filtered);
        List<Resource> resources = resourceRepository.findByIds(relationIds);
        return resources.stream()
                .filter(Objects::nonNull)
                .map(resource -> new Permission(
                        resource.getId(),
                        resource.getName(),
                        resource.getPermission(),
                        resource.getType(),
                        resource.getParentId(),
                        resource.isEnabled()))
                .toList();
    }
}
