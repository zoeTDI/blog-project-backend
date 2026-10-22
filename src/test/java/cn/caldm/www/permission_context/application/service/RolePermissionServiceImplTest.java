package cn.caldm.www.permission_context.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RolePermissionServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    @Test
    void grantResourceToRole_shouldThrow_whenRoleDisabled() {
        Role disabledRole = new Role();
        disabledRole.setId(1L);
        disabledRole.setEnabled(false);

        when(roleRepository.getRoleById(1L)).thenReturn(disabledRole);

        assertThatThrownBy(() -> rolePermissionService.grantResourceToRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("停用");
    }

    @Test
    void revokeResourceFromRole_shouldThrow_whenRoleNotFound() {
        when(roleRepository.getRoleById(1L)).thenReturn(null);

        assertThatThrownBy(() -> rolePermissionService.revokeResourceFromRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("角色不存在");
    }

    @Test
    void revokeResourceFromRole_shouldThrow_whenRoleDisabled() {
        Role disabledRole = new Role();
        disabledRole.setId(1L);
        disabledRole.setEnabled(false);

        when(roleRepository.getRoleById(1L)).thenReturn(disabledRole);

        assertThatThrownBy(() -> rolePermissionService.revokeResourceFromRole(1L, 5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("停用");
    }
}
