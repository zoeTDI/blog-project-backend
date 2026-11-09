package cn.caldm.www.auth_context.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.PostMapping;

import cn.caldm.www.auth_context.application.service.AuthApplicationService;
import cn.caldm.www.auth_context.infrastructure.web.AuthCookieManager;
import cn.caldm.www.infrastructure.annotation.Anonymous;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.security.RequirePermission;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登出端点回归钉：
 * 登出必须无条件可达——即使 accessToken / refreshToken 都已失效，
 * 用户也必须能完成登出（清除 Cookie）。
 * 防止未来有人再给 logout 挂上认证守卫，复现
 * 「refreshToken 失效导致无法进入 logout、进而无法退出」的死锁。
 */
public class AuthLogoutEndpointTest {

    private AuthApplicationService authService;
    private AuthCookieManager cookieManager;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthApplicationService.class);
        cookieManager = new AuthCookieManager();
        ReflectionTestUtils.setField(cookieManager, "cookieSecure", false);
        ReflectionTestUtils.setField(cookieManager, "cookieSameSite", "Lax");
        controller = new AuthController(authService, cookieManager);
    }

    @Test
    void logout_shouldBeAnonymousWithoutAnyGuard() throws NoSuchMethodException {
        Method logout = AuthController.class.getMethod("logout",
                HttpServletRequest.class, HttpServletResponse.class);

        assertThat(logout.getAnnotation(PostMapping.class).value()).containsExactly("/logout");
        assertThat(logout.getAnnotation(Anonymous.class))
                .as("logout 必须标记 @Anonymous，否则 token 失效时无法登出（死锁回归）")
                .isNotNull();
        assertThat(logout.getAnnotation(PreAuthorize.class))
                .as("logout 不应挂 @PreAuthorize 认证守卫")
                .isNull();
        assertThat(logout.getAnnotation(RequirePermission.class))
                .as("logout 不应挂权限守卫")
                .isNull();
    }

    @Test
    void logout_shouldSucceedAndClearCookies_whenNoTokenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Result<String> result = controller.logout(request, response);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2);
        response.getHeaders("Set-Cookie")
                .forEach(header -> assertThat(header).contains("Max-Age=0"));
        verify(authService).logout(null, null);
    }

    @Test
    void logout_shouldSucceedAndClearCookies_whenTokensExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("accessToken", "dead-at"),
                new Cookie("refreshToken", "dead-rt"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        Result<String> result = controller.logout(request, response);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2);
        response.getHeaders("Set-Cookie")
                .forEach(header -> assertThat(header).contains("Max-Age=0"));
        // 失效 token 一并交给服务层处理（黑名单 TTL<=0 自然无害）
        verify(authService).logout("dead-at", "dead-rt");
    }
}
