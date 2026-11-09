package cn.caldm.www.auth_context.interfaces.rest;

import cn.caldm.www.auth_context.application.service.AuthApplicationService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.infrastructure.web.AuthCookieManager;
import cn.caldm.www.auth_context.interfaces.dto.*;
import cn.caldm.www.infrastructure.annotation.Anonymous;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 登录、登出、状态刷新
 *
 * @author caldm
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authService;
    private final AuthCookieManager cookieManager;

    @PostMapping("/login/username-password")
    @Anonymous
    public Result<LoginResDTO> login(@Valid @RequestBody LoginUPCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByUP(command);
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/login/email-password")
    @Anonymous
    public Result<LoginResDTO> login(@Valid @RequestBody LoginEPCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByEP(command);
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/login/email-code")
    @Anonymous
    public Result<LoginResDTO> login(@Valid @RequestBody LoginECCommand command, HttpServletResponse response) {
        AuthUser authUser = authService.loginByEC(command);
        return handleLoginSuccess(response, authUser);
    }

    @PostMapping("/send-login-code")
    @Anonymous
    public Result<LoginResDTO> sendLoginCode(@Valid @RequestBody SendLoginCodeCommand command) {
        boolean sent = authService.sendLoginCode(command);
        if (!sent) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return Result.success();
    }

    @PostMapping("/logout")
    @Anonymous 
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractCookie(request, "accessToken");
        String refreshToken = extractCookie(request, "refreshToken");

        authService.logout(accessToken, refreshToken);
        cookieManager.clearTokenCookies(response);
        return Result.successMsg("登出成功");
    }

    @PostMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public Result<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refreshToken");
        TokenPair tokenPair = authService.refreshToken(refreshToken);
        // todo 处理用户假登录状态（详情见Gemini对话页面）target date 2025-8-25 record date 2025-8-25
        long accessMaxAge = (tokenPair.getAccessTokenExpiresAt() - System.currentTimeMillis()) / 1000;
        long refreshMaxAge = (tokenPair.getRefreshTokenExpiresAt() - System.currentTimeMillis()) / 1000;
        cookieManager.setTokenCookies(response,
                tokenPair.getAccessToken(), accessMaxAge,
                tokenPair.getRefreshToken(), refreshMaxAge);

        return Result.successMsg("Token 刷新成功");
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
        cookieManager.setTokenCookies(response,
                authUser.getAccessToken(), accessMaxAge,
                authUser.getRefreshToken(), refreshMaxAge);
        LoginResDTO resDTO = new LoginResDTO();
        resDTO.setId(authUser.getId());
        resDTO.setEmail(authUser.getEmail());
        resDTO.setUsername(authUser.getUsername());
        resDTO.setNickname(authUser.getNickname());
        resDTO.setRoles(authUser.getRoles());
        resDTO.setAvatar(authUser.getAvatar());
        resDTO.setAccessTokenExpiresAt(accessTokenExpiresAt);
        resDTO.setRefreshTokenExpiresAt(refreshTokenExpiresAt);

        return Result.success("登录成功", resDTO);
    }
}
