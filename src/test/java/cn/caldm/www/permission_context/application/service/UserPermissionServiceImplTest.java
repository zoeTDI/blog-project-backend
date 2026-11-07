package cn.caldm.www.permission_context.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;

/**
 * 用户权限聚合查询的语义回归钉：
 * 授权∩启用、ADMIN 全量豁免、停用角色/资源排除、先排序后建树、按钮码过滤。
 */
@ExtendWith(MockitoExtension.class)
public class UserPermissionServiceImplTest {

    @Mock
    private UserRoleRelationRepository userRoleRelationRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleResourceRelationRepository roleResourceRelationRepository;
    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private UserPermissionServiceImpl userPermissionService;

    // ==================== getUserMenuTree ====================

    @Test
    void getUserMenuTree_shouldReturnSortedTreeOfGrantedEnabledMenus() {
        stubRoles(1L, 2L, "AUTHOR", true);
        when(roleResourceRelationRepository.findResourceIdsByRoleIds(anyList()))
                .thenReturn(List.of(1L, 2L, 3L, 7L));
        when(resourceRepository.findByIds(anyList())).thenReturn(List.of(
                resource(1L, ResourceTypeEnum.DIRECTORY, 0L, 2, "router:content", true),
                resource(2L, ResourceTypeEnum.MENU, 1L, 3, "router:content:post", true),
                resource(7L, ResourceTypeEnum.MENU, 1L, 1, "router:content:review", true),
                resource(3L, ResourceTypeEnum.BUTTON, 2L, 1, "post:create", true),
                resource(8L, ResourceTypeEnum.MENU, 1L, 5, "router:content:disabled", false)));

        List<ResourceNode> tree = userPermissionService.getUserMenuTree(1L);

        // 单根（目录），子节点按 sort 升序：review(1) 在前、post(3) 在后
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getNode().getId()).isEqualTo(1L);
        List<Long> childIds = tree.get(0).getChildren().stream()
                .map(node -> node.getNode().getId())
                .toList();
        assertThat(childIds).containsExactly(7L, 2L);
    }

    @Test
    void getUserMenuTree_shouldExcludeButtonAndDisabledResources() {
        stubRoles(1L, 2L, "AUTHOR", true);
        when(roleResourceRelationRepository.findResourceIdsByRoleIds(anyList()))
                .thenReturn(List.of(1L, 3L, 8L));
        when(resourceRepository.findByIds(anyList())).thenReturn(List.of(
                resource(1L, ResourceTypeEnum.DIRECTORY, 0L, 1, "router:content", true),
                resource(3L, ResourceTypeEnum.BUTTON, 1L, 1, "post:create", true),
                resource(8L, ResourceTypeEnum.MENU, 1L, 1, "router:content:x", false)));

        List<ResourceNode> tree = userPermissionService.getUserMenuTree(1L);

        // 按钮不入树、停用菜单不入树 → 根目录无子节点
        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getChildren()).isEmpty();
    }

    @Test
    void getUserMenuTree_admin_shouldReturnOnlyGrantedMenus() {
        // 职责分离：ADMIN 无全量豁免，菜单同样来自角色授权
        stubRoles(1L, 1L, "ADMIN", true);
        when(roleResourceRelationRepository.findResourceIdsByRoleIds(anyList()))
                .thenReturn(List.of(1L, 2L, 3L, 8L));
        when(resourceRepository.findByIds(anyList())).thenReturn(List.of(
                resource(1L, ResourceTypeEnum.DIRECTORY, 0L, 1, "router:content", true),
                resource(2L, ResourceTypeEnum.MENU, 1L, 1, "router:content:post", true),
                resource(3L, ResourceTypeEnum.BUTTON, 2L, 1, "post:create", true),
                resource(8L, ResourceTypeEnum.MENU, 1L, 2, "router:content:x", false)));

        List<ResourceNode> tree = userPermissionService.getUserMenuTree(1L);

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).getChildren()).hasSize(1);
        assertThat(tree.get(0).getChildren().get(0).getNode().getId()).isEqualTo(2L);
    }

    @Test
    void getUserMenuTree_shouldExcludeDisabledRoles() {
        // 角色停用即失去其全部授权
        stubRoles(1L, 5L, "AUTHOR", false);

        List<ResourceNode> tree = userPermissionService.getUserMenuTree(1L);

        assertThat(tree).isEmpty();
        verify(roleResourceRelationRepository, never()).findResourceIdsByRoleIds(anyList());
    }

    @Test
    void getUserMenuTree_shouldReturnEmptyWhenUserHasNoRoles() {
        when(userRoleRelationRepository.getRoleRelationByUserId(1L)).thenReturn(List.of());

        List<ResourceNode> tree = userPermissionService.getUserMenuTree(1L);

        assertThat(tree).isEmpty();
        verify(roleRepository, never()).getRolesByIds(anyList());
    }

    @Test
    void getUserMenuTree_nullUserId_shouldReturnEmpty() {
        assertThat(userPermissionService.getUserMenuTree(null)).isEmpty();
        verify(userRoleRelationRepository, never()).getRoleRelationByUserId(any());
    }

    // ==================== getUserPermissionCodes ====================

    @Test
    void getUserPermissionCodes_shouldReturnSortedDistinctEnabledButtonCodes() {
        stubRoles(1L, 2L, "AUTHOR", true);
        when(roleResourceRelationRepository.findResourceIdsByRoleIds(anyList()))
                .thenReturn(List.of(2L, 3L, 4L, 5L, 6L));
        when(resourceRepository.findByIds(anyList())).thenReturn(List.of(
                resource(2L, ResourceTypeEnum.MENU, 1L, 1, "router:content:post", true),
                resource(3L, ResourceTypeEnum.BUTTON, 2L, 1, "post:create", true),
                resource(4L, ResourceTypeEnum.BUTTON, 2L, 2, "post:update", true),
                resource(5L, ResourceTypeEnum.BUTTON, 2L, 3, "post:delete", false),
                resource(6L, ResourceTypeEnum.BUTTON, 2L, 4, "", true)));

        List<String> codes = userPermissionService.getUserPermissionCodes(1L);

        // 菜单码不入列表、停用/空码被过滤、字典序升序
        assertThat(codes).containsExactly("post:create", "post:update");
    }

    @Test
    void getUserPermissionCodes_admin_shouldReturnOnlyGrantedButtonCodes() {
        stubRoles(1L, 1L, "ADMIN", true);
        when(roleResourceRelationRepository.findResourceIdsByRoleIds(anyList()))
                .thenReturn(List.of(2L, 3L, 4L, 5L));
        when(resourceRepository.findByIds(anyList())).thenReturn(List.of(
                resource(2L, ResourceTypeEnum.MENU, 1L, 1, "router:content:post", true),
                resource(3L, ResourceTypeEnum.BUTTON, 2L, 1, "post:create", true),
                resource(4L, ResourceTypeEnum.BUTTON, 2L, 2, "tag:manage", true),
                resource(5L, ResourceTypeEnum.BUTTON, 2L, 3, "post:delete", false)));

        List<String> codes = userPermissionService.getUserPermissionCodes(1L);

        assertThat(codes).containsExactly("post:create", "tag:manage");
    }

    // ==================== helpers ====================

    private void stubRoles(Long userId, Long roleId, String roleCode, boolean enabled) {
        UserRoleRelation relation = new UserRoleRelation();
        relation.setUserId(userId);
        relation.setRoleId(roleId);
        when(userRoleRelationRepository.getRoleRelationByUserId(userId)).thenReturn(List.of(relation));
        when(roleRepository.getRolesByIds(anyList())).thenReturn(List.of(role(roleId, roleCode, enabled)));
    }

    private Role role(Long id, String code, boolean enabled) {
        return Role.reconstitute(id, "角色" + id, code, 0, "", enabled);
    }

    private Resource resource(Long id, ResourceTypeEnum type, Long parentId, int sort,
                              String permission, boolean enabled) {
        return Resource.reconstitute(id, "资源" + id, permission, type, parentId, sort, "", "", "", "", enabled);
    }
}
