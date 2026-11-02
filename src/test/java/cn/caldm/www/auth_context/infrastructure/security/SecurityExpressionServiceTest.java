package cn.caldm.www.auth_context.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.caldm.www.shared_kernel.security.CurrentUser;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;

/**
 * @ss 权限码校验的语义回归钉。
 * 固定三条集中规则：未登录 false、ADMIN 豁免、精确/通配匹配。
 */
@ExtendWith(MockitoExtension.class)
public class SecurityExpressionServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private SecurityExpressionService securityExpressionService;

    @Test
    void hasPermission_exactMatch_shouldReturnTrue() {
        loginAs(List.of("AUTHOR"), List.of("post:create"));

        assertThat(securityExpressionService.hasPermission("post:create")).isTrue();
    }

    @Test
    void hasPermission_noMatchingCode_shouldReturnFalse() {
        loginAs(List.of("AUTHOR"), List.of("post:create"));

        assertThat(securityExpressionService.hasPermission("post:publish")).isFalse();
    }

    @Test
    void hasPermission_wildcardGrant_shouldCoverExactAction() {
        // 授予 post:* 应覆盖 post:create 等具体动作
        loginAs(List.of("AUTHOR"), List.of("post:*"));

        assertThat(securityExpressionService.hasPermission("post:create")).isTrue();
    }

    @Test
    void hasPermission_wildcardGrant_shouldNotCoverOtherDomain() {
        loginAs(List.of("AUTHOR"), List.of("post:*"));

        assertThat(securityExpressionService.hasPermission("user:ban")).isFalse();
    }

    @Test
    void hasPermission_adminRole_shouldBypassWithoutGrant() {
        // ADMIN 集中豁免：无需任何权限码，且对未来新增的码同样生效
        loginAs(List.of("ADMIN"), null);

        assertThat(securityExpressionService.hasPermission("post:publish")).isTrue();
        assertThat(securityExpressionService.hasPermission("not:exist")).isTrue();
    }

    @Test
    void hasPermission_unauthenticated_shouldReturnFalse() {
        // 未登录：返回 false 而不是抛异常（防御语义）
        when(currentUserProvider.getOptCurrentUser()).thenReturn(Optional.empty());

        assertThat(securityExpressionService.hasPermission("post:create")).isFalse();
    }

    @Test
    void hasPermission_emptyPermissions_shouldReturnFalse() {
        loginAs(List.of("AUTHOR"), List.of());

        assertThat(securityExpressionService.hasPermission("post:create")).isFalse();
    }

    @Test
    void hasAnyPermission_matchingOneOfSeveral_shouldReturnTrue() {
        loginAs(List.of("AUTHOR"), List.of("post:update"));

        assertThat(securityExpressionService.hasAnyPermission("post:create", "post:update")).isTrue();
    }

    @Test
    void hasAnyPermission_noneMatching_shouldReturnFalse() {
        loginAs(List.of("AUTHOR"), List.of("post:update"));

        assertThat(securityExpressionService.hasAnyPermission("post:create", "post:publish")).isFalse();
    }

    @Test
    void hasAnyPermission_emptyArgs_shouldReturnFalse() {
        // 与 hasAnyRole 的空参语义保持一致；空参时无需登录态
        assertThat(securityExpressionService.hasAnyPermission()).isFalse();
    }

    @Test
    void hasAllPermissions_allPresent_shouldReturnTrue() {
        loginAs(List.of("AUTHOR"), List.of("post:create", "post:update"));

        assertThat(securityExpressionService.hasAllPermissions("post:create", "post:update")).isTrue();
    }

    @Test
    void hasAllPermissions_oneMissing_shouldReturnFalse() {
        loginAs(List.of("AUTHOR"), List.of("post:create"));

        assertThat(securityExpressionService.hasAllPermissions("post:create", "post:update")).isFalse();
    }

    @Test
    void hasAllPermissions_emptyArgs_shouldReturnFalse() {
        // 与 hasAllRoles 的空参语义保持一致
        assertThat(securityExpressionService.hasAllPermissions()).isFalse();
    }

    /**
     * 模拟已登录用户：stub 两个入口，避免依赖 Mockito 对 default 方法的默认行为。
     * permissions 传 null 表示该用例不会触达权限列表（如 ADMIN 豁免短路），不提前 stub 以免严格模式报冗余。
     */
    private void loginAs(List<String> roles, List<String> permissions) {
        when(currentUserProvider.getOptCurrentUser())
                .thenReturn(Optional.of(new CurrentUser(1L, "tester", roles, permissions)));
        when(currentUserProvider.getRoles()).thenReturn(roles == null ? List.of() : roles);
        if (permissions != null) {
            when(currentUserProvider.getPermission()).thenReturn(permissions);
        }
    }
}
