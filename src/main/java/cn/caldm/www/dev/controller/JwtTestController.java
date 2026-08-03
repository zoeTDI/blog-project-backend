package cn.caldm.www.dev.controller;

import cn.caldm.www.auth.jwt.dto.LoginResDTO;
import cn.caldm.www.auth.jwt.utils.JwtUtils;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.login.domain.SysUser;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 *
 * 带token验证的控制器
 *
 * @author caldm
 */
@Slf4j
@RestController
public class JwtTestController {
    static Map<Integer, SysUser> userMap = new HashMap<>();

    static {
        SysUser user1 = new SysUser();
        user1.setId(1L);
        user1.setUsername("admin@caldm.cn");
        user1.setPassword("123456");
        userMap.put(1, user1);
        SysUser user2 = new SysUser();
        user2.setId(2L);
        user2.setUsername("李四");
        user2.setPassword("abcdefg");
        userMap.put(2, user2);
    }

    @RequestMapping("/login")
    public Result<LoginResDTO> login(@RequestBody SysUser user, HttpServletResponse response) {

        for (SysUser dbUser: userMap.values()) {
            if (dbUser.getUsername().equals(user.getUsername())
                    && dbUser.getPassword().equals(user.getPassword())) {
                LogUtils.info("登录成功！生成双 token！");
                user.setId(dbUser.getId());

                String accessToken = JwtUtils.createAccessToken(user);
                String refreshToken = JwtUtils.createRefreshToken(user);

                ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(3600)
                        .sameSite("Lax")
                        .build();

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(7L * 24 * 60 * 60)
                        .sameSite("Lax")
                        .build();

                // 将 ResponseCookie 写入 HttpServletResponse 的 Set-Cookie 响应头
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                LoginResDTO dto = new LoginResDTO();
                dto.setId(dbUser.getId());
                dto.setEmail("example@email.com");
                dto.setUsername(dbUser.getUsername());
                dto.setNickname("Admin");
                dto.setRole("暂未实现该功能");
                List<String> menus = new ArrayList<>();
                menus.add("暂未实现该功能");
                dto.setMenus(menus);
                return Result.success(dto);
            }
        }
        return Result.error(ResultCodeEnum.BAD_REQUEST);
    }

    @RequestMapping("/secure/refresh")
    public Result<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error(ResultCodeEnum.UNAUTHORIZED, "未携带 Refresh Token");
        }

        Map<String, Claim> claims = JwtUtils.verifyToken(refreshToken);
        if (claims == null) {
            return Result.error(ResultCodeEnum.UNAUTHORIZED, "令牌类型错误，无法用于刷新");
        }

        Long id = claims.get("id").asLong();
        String username = claims.get("username").asString();

        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);

        String newAccessToken = JwtUtils.createAccessToken(user);
        JwtUtils.invalidateToken(refreshToken);
        String newRefreshToken = JwtUtils.createRefreshToken(user);

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return Result.successMsg("Token 刷新成功");
    }

    @RequestMapping("/secure/logout")
    public Result<String> logout(HttpServletRequest request ) {
        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null && !accessToken.isEmpty()) {
            JwtUtils.invalidateToken(accessToken);
        }
        if (refreshToken != null && !refreshToken.isEmpty()) {
            JwtUtils.invalidateToken(refreshToken);
        }
        return Result.successMsg("登出成功");
    }

    /**
     * 查询 用户信息，登录后携带JWT才能访问
     */
    @RequestMapping("/secure/getUserInfo")
    public Result<String> getUserInfo(HttpServletRequest request) {
        Long id = (Long) request.getAttribute("id");
        String username = request.getAttribute("username").toString();
        return Result.success("当前用户信息id=" + id + ",username=" + username);
    }
}
