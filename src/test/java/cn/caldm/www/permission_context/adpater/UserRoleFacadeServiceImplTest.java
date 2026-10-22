package cn.caldm.www.permission_context.adpater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.model.UserRoleRelation;
import cn.caldm.www.permission_context.domain.repository.ResourceRepository;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.RoleResourceRelationRepository;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import cn.caldm.www.user_context.domain.modal.UserRole;

@ExtendWith(MockitoExtension.class)
public class UserRoleFacadeServiceImplTest {

    @Mock
    private UserRoleRelationRepository userRoleRelationRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleResourceRelationRepository roleResourceRelationRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private UserRoleFacadeServiceImpl userRoleFacadeService;

    @Test
    void getUserRolesById_shouldReturnRolesFromRelations() {
        // userId 与 roleId 取值不同，可暴露"拿 userId 当 roleId 查询"的错误
        Long userId = 100L;
        UserRoleRelation relation1 = new UserRoleRelation();
        relation1.setUserId(userId);
        relation1.setRoleId(1L);
        UserRoleRelation relation2 = new UserRoleRelation();
        relation2.setUserId(userId);
        relation2.setRoleId(2L);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setCode("ADMIN");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setCode("AUTHOR");

        when(userRoleRelationRepository.getRoleRelationByUserId(userId))
                .thenReturn(List.of(relation1, relation2));
        when(roleRepository.getRolesByIds(List.of(1L, 2L)))
                .thenReturn(List.of(role1, role2));

        List<UserRole> result = userRoleFacadeService.getUserRolesById(userId);

        assertThat(result).extracting(UserRole::getId).containsExactly(1L, 2L);
    }

    @Test
    void assignRoleToUser_shouldThrow_whenRoleNotFound() {
        when(roleRepository.getRoleByCode("NO_SUCH_ROLE")).thenReturn(null);

        assertThatThrownBy(() -> userRoleFacadeService.assignRoleToUser(100L, "NO_SUCH_ROLE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("角色不存在");

        verify(userRoleRelationRepository, never()).assignRoleToUser(100L, 1L);
    }

    @Test
    void assignRoleToUser_shouldThrow_whenRoleDisabled() {
        Role disabledRole = new Role();
        disabledRole.setId(1L);
        disabledRole.setCode("DISABLED_ROLE");
        disabledRole.setEnabled(false);

        when(roleRepository.getRoleByCode("DISABLED_ROLE")).thenReturn(disabledRole);

        assertThatThrownBy(() -> userRoleFacadeService.assignRoleToUser(100L, "DISABLED_ROLE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("停用");

        verify(userRoleRelationRepository, never()).assignRoleToUser(100L, 1L);
    }
}
