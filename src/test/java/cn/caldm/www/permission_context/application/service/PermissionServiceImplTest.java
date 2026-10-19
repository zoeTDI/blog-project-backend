package cn.caldm.www.permission_context.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.permission_context.application.command.UpdateResourceCommand;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleResourceRelationRepository roleResourceRelationRepository;
    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void updateResource_shouldRejectDirectoryUnderMenu() {
        // 资源当前是根目录（parentId=0），试图挂到菜单（parentId=5）下：
        // 若 validateParent 在 update 之前基于旧 parentId 校验，该非法层级会被放行
        Resource directory = Resource.reconstitute(
                1L, "系统管理", "system", ResourceTypeEnum.DIRECTORY,
                0L, 1, "/system", null, null, "menu.system", true);
        Resource menuParent = Resource.reconstitute(
                5L, "博客菜单", "blog:view", ResourceTypeEnum.MENU,
                0L, 1, "/blog", "BlogView", "icon", "menu.blog", true);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(directory));
        when(resourceRepository.findById(5L)).thenReturn(Optional.of(menuParent));

        UpdateResourceCommand command = new UpdateResourceCommand();
        command.setId(1L);
        command.setName("系统管理");
        command.setPermission("system");
        command.setParentId(5L);
        command.setSort(1);
        command.setPath("/system");
        command.setComponent(null);
        command.setIcon(null);
        command.setTitleKey("menu.system");

        assertThatThrownBy(() -> permissionService.updateResource(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("非法的资源父子关系");
    }

    @Test
    void revokeResourceFromRole_shouldThrow_whenRoleNotFound() {
        when(roleRepository.getRoleById(1L)).thenReturn(null);

        assertThatThrownBy(() -> permissionService.revokeResourceFromRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("角色不存在");
    }

    @Test
    void grantResourceToRole_shouldThrow_whenRoleDisabled() {
        Role disabledRole = new Role();
        disabledRole.setId(1L);
        disabledRole.setEnabled(false);

        when(roleRepository.getRoleById(1L)).thenReturn(disabledRole);

        assertThatThrownBy(() -> permissionService.grantResourceToRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("停用");
    }

    @Test
    void revokeResourceFromRole_shouldThrow_whenRoleDisabled() {
        Role disabledRole = new Role();
        disabledRole.setId(1L);
        disabledRole.setEnabled(false);

        when(roleRepository.getRoleById(1L)).thenReturn(disabledRole);

        assertThatThrownBy(() -> permissionService.revokeResourceFromRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("停用");
    }
}
