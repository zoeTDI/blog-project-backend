package cn.caldm.www.permission_context.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import cn.caldm.www.permission_context.utils.ResourceUtils;
import cn.caldm.www.shared_kernel.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private static final String ADMIN_ROLE_CODE = "ADMIN";

    private final UserRoleRelationRepository userRoleRelationRepository;
    private final RoleRepository roleRepository;
    private final RoleResourceRelationRepository roleResourceRelationRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public List<ResourceNode> getUserMenuTree(Long userId) {
        List<Resource> menus = getAccessibleResources(userId).stream()
                .filter(res -> res.isDirectory() || res.isMenu())
                .toList();
        return ResourceUtils.buildTree(sortByWeight(menus));
    }

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        return getAccessibleResources(userId).stream()
                .filter(Resource::isButton)
                .map(Resource::getPermission)
                .filter(code -> !StringUtils.isBlank(code))
                .map(String::trim)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * 用户可访问的资源全集：停用角色与停用资源一律排除。
     * ADMIN 角色 → 全量（集中豁免，不依赖授权行）；其余 → 角色授权 ∩ 启用。
     */
    private List<Resource> getAccessibleResources(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<Role> roles = getEnabledRoles(userId);
        if (isAdmin(roles)) {
            return resourceRepository.findAll().stream()
                    .filter(Resource::isEnabled)
                    .toList();
        }
        List<Long> roleIds = roles.stream().map(Role::getId).filter(Objects::nonNull).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<Long> resourceIds = roleResourceRelationRepository.findResourceIdsByRoleIds(roleIds);
        if (resourceIds == null || resourceIds.isEmpty()) {
            return List.of();
        }
        return resourceRepository.findByIds(resourceIds).stream()
                .filter(Resource::isEnabled)
                .toList();
    }

    /** 仅启用角色参与授权计算：角色停用即失去其全部授权 */
    private List<Role> getEnabledRoles(Long userId) {
        List<UserRoleRelation> relations = userRoleRelationRepository.getRoleRelationByUserId(userId);
        if (relations == null || relations.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = relations.stream()
                .map(UserRoleRelation::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return roleRepository.getRolesByIds(roleIds).stream()
                .filter(Role::isEnabled)
                .toList();
    }

    private boolean isAdmin(List<Role> roles) {
        for (Role role : roles) {
            if (ADMIN_ROLE_CODE.equals(role.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * sort 升序（null 视为 0），同值按 id 升序。
     */
    private List<Resource> sortByWeight(List<Resource> resources) {
        return resources.stream()
                .sorted(Comparator
                        .comparing(Resource::getSort, Comparator.nullsFirst(Integer::compareTo))
                        .thenComparing(Resource::getId, Comparator.nullsFirst(Long::compareTo)))
                .toList();
    }

}
