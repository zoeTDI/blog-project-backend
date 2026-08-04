package cn.caldm.www.auth_context.interfaces.rest;

import cn.caldm.www.auth_context.application.service.AuthApplicationService;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.interfaces.dto.LoginReqDTO;
import cn.caldm.www.auth_context.interfaces.dto.LoginResDTO;
import cn.caldm.www.common.domain.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * 登录、登出、状态刷新
 *
 * @author caldm
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthApplicationService authService;

    @RequestMapping("/login")
    public Result<LoginResDTO> login(@RequestBody LoginReqDTO request, HttpServletResponse response) {

        TokenPair tokenPair = authService.login(request.getUsername(), request.getPassword());

        setCookie(response, "accessToken", tokenPair.getAccessToken(), 3600);
        setCookie(response, "refreshToken", tokenPair.getRefreshToken(), 7 * 24 * 3600);

        return Result.successMsg("登录成功");
    }

    @RequestMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response ) {
        String accessToken = extractCookie(request, "accessToken");
        String refreshToken = extractCookie(request, "refreshToken");

        authService.logout(accessToken, refreshToken);

        // 清除客户端 Cookie
        setCookie(response, "accessToken", "", 0);
        setCookie(response, "refreshToken", "", 0);

        return Result.successMsg("登出成功");
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refreshToken");
        TokenPair tokenPair = authService.refreshToken(refreshToken);

        setCookie(response, "accessToken", tokenPair.getAccessToken(), 3600);
        setCookie(response, "refreshToken", tokenPair.getRefreshToken(), 7 * 24 * 3600);

        return Result.successMsg("Token 刷新成功");
    }

    private void setCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // 生产环境建议设为 true
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
