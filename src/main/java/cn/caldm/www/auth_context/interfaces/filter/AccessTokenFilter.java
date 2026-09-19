package cn.caldm.www.auth_context.interfaces.filter;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import com.auth0.jwt.interfaces.Claim;
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
public class AccessTokenFilter extends OncePerRequestFilter {

    private final AuthUserFacadeService authUserFacadeService;
    private final JwtTokenProvider jwtTokenProvider;

    public AccessTokenFilter(AuthUserFacadeService authUserFacadeService, JwtTokenProvider jwtTokenProvider) {
        this.authUserFacadeService = authUserFacadeService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String accessToken = getAccessToken(request);
            if (accessToken != null) {
                Map<String, Claim> data = jwtTokenProvider.verifyToken(accessToken);
                if (data != null && "access".equals(data.get("type").asString())) {
                    Long userId = data.get("id").asLong();
                    AuthUser authUser = authUserFacadeService.getCredentialById(userId);

                    if (authUser != null
                            && !SysUserStatusEnum.DISABLED.equals(authUser.getStatus())
                            && !SysUserDeletedEnum.DELETED.equals(authUser.getDeleted())) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                authUser,
                                null,
                                authUser.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }


    }
    
    private String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
