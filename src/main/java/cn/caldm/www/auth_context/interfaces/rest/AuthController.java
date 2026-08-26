package cn.caldm.www.auth_context.interfaces.rest;

import cn.caldm.www.auth_context.application.service.AuthApplicationService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.interfaces.dto.*;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login/username-password")
    public Result<LoginResDTO> login(@Valid @RequestBody LoginUPCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByUP(command);
        if (authUser == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/login/email-password")
    public Result<LoginResDTO> login(@Valid @RequestBody LoginEPCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByEP(command);
        if (authUser == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/login/email-code")
    public Result<LoginResDTO> login(@Valid @RequestBody LoginECCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByEC(command);
        if (authUser == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/send-login-code")
    public Result<LoginResDTO> sendLoginCode(@Valid @RequestBody SendLoginCodeCommand command) {
        boolean sent = authService.sendLoginCode(command);
        if (!sent) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractCookie(request, "accessToken");
        String refreshToken = extractCookie(request, "refreshToken");

        authService.logout(accessToken, refreshToken);

        // 清除客户端 Cookie
        if (accessToken != null) {
            setCookie(response, "accessToken", "", 0);
        }
        if (refreshToken != null) {
            setCookie(response, "refreshToken", "", 0);
        }

        return Result.successMsg("登出成功");
    }

    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refreshToken");
        TokenPair tokenPair = authService.refreshToken(refreshToken);
        // todo 处理用户假登录状态（详情见Gemini对话页面）target date 2025-8-25 record date 2025-8-25
        long accessMaxAge = (tokenPair.getAccessTokenExpiresAt() - System.currentTimeMillis()) / 1000;
        long refreshMaxAge = (tokenPair.getRefreshTokenExpiresAt() - System.currentTimeMillis()) / 1000;
        setCookie(response, "accessToken", tokenPair.getAccessToken(), accessMaxAge);
        setCookie(response, "refreshToken", tokenPair.getRefreshToken(), refreshMaxAge);

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

    private Result<LoginResDTO> handleLoginSuccess(HttpServletResponse response, AuthUser authUser) {
        Long accessTokenExpiresAt = authUser.getAccessTokenExpiresAt();
        Long refreshTokenExpiresAt = authUser.getRefreshTokenExpiresAt();
        long accessMaxAge = (accessTokenExpiresAt - System.currentTimeMillis()) / 1000;
        long refreshMaxAge = (refreshTokenExpiresAt - System.currentTimeMillis()) / 1000;
        setCookie(response, "accessToken", authUser.getAccessToken(), accessMaxAge);
        setCookie(response, "refreshToken", authUser.getRefreshToken(), refreshMaxAge);
        LoginResDTO resDTO = new LoginResDTO();
        resDTO.setId(authUser.getId());
        resDTO.setEmail(authUser.getEmail());
        resDTO.setUsername(authUser.getUsername());
        resDTO.setNickname(authUser.getNickname());
        resDTO.setRoles(authUser.getRoles());
        resDTO.setAvatar(authUser.getAvatar());
        resDTO.setMenus(authUser.getMenus());
        resDTO.setAccessTokenExpiresAt(accessTokenExpiresAt);
        resDTO.setRefreshTokenExpiresAt(refreshTokenExpiresAt);

        return Result.success("登录成功", resDTO);
    }
}
