package cn.caldm.www.permission_context.adpater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

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
import cn.caldm.www.user_context.domain.model.UserRole;

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

    // ==================== getUserRolesByUserIds ====================

    @Test
    void getUserRolesByUserIds_shouldReturnEmpty_whenUserIdsEmpty() {
        assertThat(userRoleFacadeService.getUserRolesByUserIds(List.of())).isEmpty();
        verify(userRoleRelationRepository, never()).getRoleRelationsByUserIds(List.of());
    }

    @Test
    void getUserRolesByUserIds_shouldReturnEmpty_whenNoRelations() {
        when(userRoleRelationRepository.getRoleRelationsByUserIds(List.of(100L, 200L)))
                .thenReturn(List.of());

        assertThat(userRoleFacadeService.getUserRolesByUserIds(List.of(100L, 200L))).isEmpty();
    }

    @Test
    void getUserRolesByUserIds_shouldGroupRolesByUser() {
        UserRoleRelation r1 = new UserRoleRelation();
        r1.setUserId(100L);
        r1.setRoleId(1L);
        UserRoleRelation r2 = new UserRoleRelation();
        r2.setUserId(200L);
        r2.setRoleId(2L);
        UserRoleRelation r3 = new UserRoleRelation();
        r3.setUserId(200L);
        r3.setRoleId(3L);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setCode("ADMIN");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setCode("AUTHOR");
        Role role3 = new Role();
        role3.setId(3L);
        role3.setCode("REVIEWER");

        when(userRoleRelationRepository.getRoleRelationsByUserIds(List.of(100L, 200L)))
                .thenReturn(List.of(r1, r2, r3));
        when(roleRepository.getRolesByIds(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(role1, role2, role3));

        var result = userRoleFacadeService.getUserRolesByUserIds(List.of(100L, 200L));

        assertThat(result).containsOnlyKeys(100L, 200L);
        assertThat(result.get(100L)).extracting(UserRole::getCode).containsExactly("ADMIN");
        assertThat(result.get(200L)).extracting(UserRole::getCode).containsExactly("AUTHOR", "REVIEWER");
    }

    @Test
    void getUserRolesByUserIds_shouldSkipRelationWhoseRoleMissing() {
        UserRoleRelation r1 = new UserRoleRelation();
        r1.setUserId(100L);
        r1.setRoleId(1L);
        // roleId=99 的脏关系：角色已不存在
        UserRoleRelation r2 = new UserRoleRelation();
        r2.setUserId(100L);
        r2.setRoleId(99L);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setCode("ADMIN");

        when(userRoleRelationRepository.getRoleRelationsByUserIds(List.of(100L)))
                .thenReturn(List.of(r1, r2));
        when(roleRepository.getRolesByIds(List.of(1L, 99L)))
                .thenReturn(List.of(role1));

        var result = userRoleFacadeService.getUserRolesByUserIds(List.of(100L));

        assertThat(result.get(100L)).extracting(UserRole::getCode).containsExactly("ADMIN");
    }
}
