package cn.caldm.www.auth_context.interfaces.filter;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 *
 *
 *
 * @author caldm
 */
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final AuthUserFacadeService authUserFacadeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public RefreshTokenFilter(AuthUserFacadeService authUserFacadeService, JwtTokenProvider jwtTokenProvider) {
        this.authUserFacadeService = authUserFacadeService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");


        String refreshToken = getRefreshToken(request);
        if (refreshToken == null) {
            writeErrorResponse(response);
            return;
        }

        Map<String, Claim> data = jwtTokenProvider.verifyToken(refreshToken);
        if (data == null || !"refresh".equals(data.get("type").asString())) {
            writeErrorResponse(response);
            return;
        }

        AuthUser authUser = authUserFacadeService.getCredentialById(data.get("id").asLong());
        if (authUser == null
                || SysUserStatusEnum.DISABLED.equals(authUser.getStatus())
                || SysUserDeletedEnum.DELETED.equals(authUser.getDeleted())) {
            writeErrorResponse(response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authUser,
                    null,
                    authUser.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }

    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Object> result = Result.error(ResultCodeEnum.REFRESH_FAILED, "Authentication failed.");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
