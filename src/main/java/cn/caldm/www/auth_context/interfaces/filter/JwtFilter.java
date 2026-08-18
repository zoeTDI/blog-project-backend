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
            // 访问文件资源端口 todo 暂未实现
            "/api/file/**"
    );

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        for (String pattern : WHITELIST) {
            if (pathMatcher.match(pattern, requestUri)) {
                // 校验通过，直接放行
                filterChain.doFilter(request, response);
                return;
            }
        }

        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if (accessToken == null || accessToken.isEmpty()) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "未携带Token授权信息");
            return;
        }

        Map<String, Claim> userData = jwtTokenProvider.verifyToken(accessToken);
        if (userData == null) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "Token不合法、已过期或已注销");
            return;
        }

        Claim tokenTypeClaim = userData.get("type");
        if (tokenTypeClaim == null || !"access".equals(tokenTypeClaim.asString())) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "令牌类型错误，无法访问业务接口");
            return;
        }

        Long id = userData.get("id").asLong();
        String username = userData.get("username").asString();

        AuthUser user = authUserFacadeService.getCredentialById(id);
        if (user == null
            || SysUserStatusEnum.DISABLED.equals(user.getStatus())
            || SysUserDeletedEnum.DELETED.equals(user.getDeleted())
        ) {
            writeErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "该账户已被封禁或已注销");
            return;
        }

        try {
            SecurityContextHolder.Manager.setCurrentUser(id, username);

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.Manager.clear();
        }
    }

    private void writeErrorResponse(HttpServletResponse response, ResultCodeEnum resultCode, String customMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result = Result.error(resultCode, customMessage);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
