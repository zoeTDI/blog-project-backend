package cn.caldm.www.auth_context.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证 Cookie 策略的唯一出口。
 * 统一下发与清除 accessToken / refreshToken 两个 Cookie 的属性策略
 * （httpOnly / secure / path / sameSite），供 Controller 与 Filter 共用。
 *
 * @author caldm
 */
@Component
public class AuthCookieManager {

    /**
     * accessToken Cookie 名
     */
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    /**
     * refreshToken Cookie 名
     */
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    /**
     * Cookie Secure 标记：本地默认 false，生产环境务必配置为 true（HTTPS 下才下发）
     */
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * Cookie SameSite：Lax / Strict / None，None 必须配合 secure=true 才会被浏览器接受
     */
    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    /**
     * 下发双令牌 Cookie（登录成功、刷新成功场景）
     *
     * @param response      HTTP 响应
     * @param accessToken   access token 值
     * @param accessMaxAge  access Cookie 存活秒数
     * @param refreshToken  refresh token 值
     * @param refreshMaxAge refresh Cookie 存活秒数
     */
    public void setTokenCookies(HttpServletResponse response,
            String accessToken, long accessMaxAge,
            String refreshToken, long refreshMaxAge) {
        addCookie(response, ACCESS_TOKEN_COOKIE, accessToken, accessMaxAge);
        addCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, refreshMaxAge);
    }

    /**
     * 清除双令牌 Cookie（Max-Age=0，浏览器立即删除）
     * 场景：登出成功；刷新失败（refreshToken 失效/黑名单/用户禁用删除）时的强制登出。
     * 幂等：即使客户端本就没有 Cookie，重复清除也无副作用。
     */
    public void clearTokenCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", 0);
    }

    /**
     * 按统一策略写入单个 Cookie
     */
    private void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookieSameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
