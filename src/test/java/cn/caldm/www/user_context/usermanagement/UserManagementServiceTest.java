package cn.caldm.www.user_context.usermanagement;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.domain.model.UserRole;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.ports.UserRoleFacadeService;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleFacadeService userRoleFacadeService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private UserManagementService userManagementService;

    // ==================== getUserPage ====================

    @Test
    void getUserPage_shouldThrow_whenPageLessThan1() {
        assertThatThrownBy(() -> userManagementService.getUserPage(0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("page");
    }

    @Test
    void getUserPage_shouldThrow_whenSizeOutOfRange() {
        assertThatThrownBy(() -> userManagementService.getUserPage(1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
        assertThatThrownBy(() -> userManagementService.getUserPage(1, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
    }

    @Test
    void getUserPage_shouldSkipRoleQuery_whenPageEmpty() {
        PageResult<SysUser> emptyPage = new PageResult<>(List.of(), 0, 1, 10, 0);
        when(userRepository.findPage(1, 10)).thenReturn(emptyPage);

        PageResult<SysUser> result = userManagementService.getUserPage(1, 10);

        assertThat(result.getRecords()).isEmpty();
        verify(userRoleFacadeService, never()).getUserRolesByUserIds(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void getUserPage_shouldPopulateRolesInBatch() {
        SysUser alice = new SysUser();
        alice.setId(1L);
        SysUser bob = new SysUser();
        bob.setId(2L);
        PageResult<SysUser> page = new PageResult<>(List.of(alice, bob), 2, 1, 10, 1);
        when(userRepository.findPage(1, 10)).thenReturn(page);
        when(userRoleFacadeService.getUserRolesByUserIds(List.of(1L, 2L)))
                .thenReturn(Map.of(1L, List.of(new UserRole(10L, "ADMIN"))));

        PageResult<SysUser> result = userManagementService.getUserPage(1, 10);

        assertThat(result.getRecords().get(0).getRoles()).extracting(UserRole::getCode).containsExactly("ADMIN");
        // 无角色的用户得到空列表而非 null
        assertThat(result.getRecords().get(1).getRoles()).isEmpty();
    }

    @Test
    void getUserPage_shouldNotFail_whenFacadeReturnsEmptyMap() {
        SysUser alice = new SysUser();
        alice.setId(1L);
        PageResult<SysUser> page = new PageResult<>(List.of(alice), 1, 1, 10, 1);
        when(userRepository.findPage(1, 10)).thenReturn(page);
        when(userRoleFacadeService.getUserRolesByUserIds(List.of(1L))).thenReturn(Map.of());

        PageResult<SysUser> result = userManagementService.getUserPage(1, 10);

        assertThat(result.getRecords().get(0).getRoles()).isEmpty();
    }
}
