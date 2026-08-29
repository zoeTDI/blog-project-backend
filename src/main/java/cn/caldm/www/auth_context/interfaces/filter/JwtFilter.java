package cn.caldm.www.auth_context.interfaces.filter;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private AuthUserFacadeService authUserFacadeService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 白名单
    private static final List<String> WHITELIST = Arrays.asList(
            // 放行登录端口
            "/api/auth/login",
            "/api/auth/login/username-password",
            "/api/auth/login/email-password",
            "/api/auth/login/email-code",
            "/api/auth/send-login-code",
            "/api/**/public/**",
            // 访问文件资源端口 todo 暂未实现
            "/api/file/**");

    private static final String REFRESH_PATH = "/api/auth/refresh";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        // white list verify
        if (pathVerify(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (REFRESH_PATH.equals(requestUri)) {

            // 刷新token
            String refreshToken = getRefreshToken(request);
            if (refreshToken == null) {
                writeErrorResponse(response, ResultCodeEnum.REFRESH_FAILED);
                return;
            }
            Map<String, Claim> data = jwtTokenProvider.verifyToken(refreshToken);
            if (data == null) {
                writeErrorResponse(response, ResultCodeEnum.REFRESH_FAILED);
                return;
            }
            String tokenType = data.get("type").asString();
            if (!("refresh".equals(tokenType))) {
                writeErrorResponse(response, ResultCodeEnum.REFRESH_FAILED);
                return;
            }
            Long id = data.get("id").asLong();
            AuthUser authUser = authUserFacadeService.getCredentialById(id);
            if (authUser == null
                    || SysUserStatusEnum.DISABLED.equals(authUser.getStatus())
                    || SysUserDeletedEnum.DELETED.equals(authUser.getDeleted())) {
                writeErrorResponse(response, ResultCodeEnum.REFRESH_FAILED);
                return;
            }
            try {
                SecurityContextHolder.Manager.setCurrentUser(authUser.getId(), authUser.getUsername());
                    filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.Manager.clear();
            }

        } else {
            String accessToken = getAccessToken(request);
            if (accessToken == null) {
                writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
                return;
            }
            Map<String, Claim> data = jwtTokenProvider.verifyToken(accessToken);
            if (data == null) {
                writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
                return;
            }
            String type = data.get("type").asString();
            if (!("access".equals(type))) {
                writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
                return;
            }
            Long id = data.get("id").asLong();
            AuthUser authUser = authUserFacadeService.getCredentialById(id);
            if (authUser == null
                    || SysUserStatusEnum.DISABLED.equals(authUser.getStatus())
                    || SysUserDeletedEnum.DELETED.equals(authUser.getDeleted())) {
                writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED);
                return;
            }
            try {
                SecurityContextHolder.Manager.setCurrentUser(authUser.getId(), authUser.getUsername());
                filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.Manager.clear();
            }
        }
    }

    private void writeErrorResponse(HttpServletResponse response, ResultCodeEnum resultCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result = Result.error(resultCode, "Authentication failed.");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * Check if the request URI matches any pattern in the white list.
     *
     * @param requestUri the URI of the current request.
     * @return true if the URI matches a white list pattern, false otherwise.
     */
    private boolean pathVerify(String requestUri) {
        for (String pattern : WHITELIST) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
