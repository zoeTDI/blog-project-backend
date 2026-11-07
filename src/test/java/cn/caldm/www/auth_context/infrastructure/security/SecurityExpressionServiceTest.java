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
    void hasPermission_adminWithoutGrant_shouldReturnFalse() {
        // 职责分离：ADMIN 不再豁免，未授权码一律拒绝
        loginAs(List.of("ADMIN"), null);

        assertThat(securityExpressionService.hasPermission("post:publish")).isFalse();
        assertThat(securityExpressionService.hasPermission("not:exist")).isFalse();
    }

    @Test
    void hasPermission_adminWithGrant_shouldReturnTrue() {
        // ADMIN 与普通角色同规则：显式授权的码正常放行
        loginAs(List.of("ADMIN"), List.of("user:manage"));

        assertThat(securityExpressionService.hasPermission("user:manage")).isTrue();
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
     * 模拟已登录用户：hasPermission 只读取登录态与权限列表（豁免移除后不再读角色），
     * 故只 stub 这两个入口，避免严格模式报冗余桩。
     */
    private void loginAs(List<String> roles, List<String> permissions) {
        when(currentUserProvider.getOptCurrentUser())
                .thenReturn(Optional.of(new CurrentUser(1L, "tester", roles, permissions)));
        if (permissions != null) {
            when(currentUserProvider.getPermission()).thenReturn(permissions);
        }
    }
}
