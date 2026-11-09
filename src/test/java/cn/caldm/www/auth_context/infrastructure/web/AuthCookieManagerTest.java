package cn.caldm.www.auth_context.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 认证 Cookie 策略单元测试：
 * 下发与清除都必须携带统一策略属性（HttpOnly / SameSite / Path），
 * 清除时必须 Max-Age=0 令浏览器立即删除。
 */
public class AuthCookieManagerTest {

    private final AuthCookieManager cookieManager = new AuthCookieManager();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cookieManager, "cookieSecure", false);
        ReflectionTestUtils.setField(cookieManager, "cookieSameSite", "Lax");
    }

    @Test
    void setTokenCookies_shouldWriteBothTokensWithPolicy() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieManager.setTokenCookies(response, "at-1", 3600, "rt-1", 604800);

        List<String> headers = response.getHeaders("Set-Cookie");
        assertThat(headers).hasSize(2);
        assertThat(headers.get(0))
                .contains("accessToken=at-1", "Max-Age=3600", "HttpOnly", "SameSite=Lax", "Path=/");
        assertThat(headers.get(1))
                .contains("refreshToken=rt-1", "Max-Age=604800", "HttpOnly", "SameSite=Lax", "Path=/");
    }

    @Test
    void clearTokenCookies_shouldExpireBothCookiesImmediately() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieManager.clearTokenCookies(response);

        List<String> headers = response.getHeaders("Set-Cookie");
        assertThat(headers).hasSize(2);
        // Max-Age=0 是浏览器立即删除 Cookie 的唯一信号
        assertThat(headers.get(0)).contains("accessToken=", "Max-Age=0", "HttpOnly", "SameSite=Lax", "Path=/");
        assertThat(headers.get(1)).contains("refreshToken=", "Max-Age=0", "HttpOnly", "SameSite=Lax", "Path=/");
    }

    @Test
    void setTokenCookies_shouldMarkSecure_whenConfigured() {
        ReflectionTestUtils.setField(cookieManager, "cookieSecure", true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieManager.setTokenCookies(response, "at-2", 1, "rt-2", 1);

        response.getHeaders("Set-Cookie")
                .forEach(header -> assertThat(header).contains("Secure"));
    }

    @Test
    void setTokenCookies_shouldNotMarkSecure_byDefault() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieManager.setTokenCookies(response, "at-3", 1, "rt-3", 1);

        response.getHeaders("Set-Cookie")
                .forEach(header -> assertThat(header).doesNotContain("Secure"));
    }
}
