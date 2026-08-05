package cn.caldm.www.auth_context.interfaces.rest;

import cn.caldm.www.auth_context.application.service.AuthApplicationService;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.interfaces.dto.*;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public Result<LoginResDTO> login(@RequestBody LoginUPReqDTO reqDTO, HttpServletResponse response) {
        String username = reqDTO.getUsername();
        String password = reqDTO.getPassword();
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        TokenPair tokenPair = authService.loginByUP(username, password);
        if (tokenPair == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, tokenPair);
    }

    @PostMapping("/login/email-password")
    public Result<LoginResDTO> login(@RequestBody LoginEPReqDTO reqDTO, HttpServletResponse response) {
        String email = reqDTO.getEmail();
        String password = reqDTO.getPassword();
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        TokenPair tokenPair = authService.loginByEP(email, password);
        if (tokenPair == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, tokenPair);
    }

    @PostMapping("/login/email-code")
    public Result<LoginResDTO> login(@RequestBody LoginECReqDTO reqDTO, HttpServletResponse response) {
        String email = reqDTO.getEmail();
        String code = reqDTO.getCode();
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        TokenPair tokenPair = authService.loginByEC(email, code);
        if (tokenPair == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return handleLoginSuccess(response, tokenPair);
    }

    @PostMapping("/send-login-code")
    public Result<LoginResDTO> sendLoginCode(@RequestBody SendLoginCodeDTO reqDto) {
        String email = reqDto.getEmail();
        if (email == null || email.isEmpty()) {
            return null;
        }
        boolean sent = authService.sendLoginCode(email);
        if (!sent) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return Result.success();
    }


    @PostMapping("/logout")
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

    private Result<LoginResDTO> handleLoginSuccess(HttpServletResponse response, TokenPair tokenPair) {
        setCookie(response, "accessToken", tokenPair.getAccessToken(), 3600);
        setCookie(response, "refreshToken", tokenPair.getRefreshToken(), 7 * 24 * 3600);
        return Result.successMsg("登录成功");
    }
}
