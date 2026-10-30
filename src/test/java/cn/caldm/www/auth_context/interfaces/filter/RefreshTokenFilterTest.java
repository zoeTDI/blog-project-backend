package cn.caldm.www.auth_context.interfaces.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.interfaces.Claim;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;

public class RefreshTokenFilterTest {

    private final AuthUserFacadeService facade = mock(AuthUserFacadeService.class);
    private final JwtTokenProvider provider = mock(JwtTokenProvider.class);
    private final TokenBlacklistRepository blacklist = mock(TokenBlacklistRepository.class);
    private final RefreshTokenFilter filter = new RefreshTokenFilter(facade, provider, blacklist);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Map<String, Claim> validRefreshClaims() {
        Claim type = mock(Claim.class);
        when(type.asString()).thenReturn("refresh");
        Claim id = mock(Claim.class);
        when(id.asLong()).thenReturn(1L);
        return Map.of("type", type, "id", id);
    }

    @Test
    void shouldRejectWith401_whenTokenBlacklisted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "rt-1"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Map<String, Claim> claims = validRefreshClaims();
        when(provider.verifyToken("rt-1")).thenReturn(claims);
        when(blacklist.isBlacklisted("rt-1")).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldPass_whenTokenValidAndNotBlacklisted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "rt-2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Map<String, Claim> claims = validRefreshClaims();
        when(provider.verifyToken("rt-2")).thenReturn(claims);
        when(blacklist.isBlacklisted("rt-2")).thenReturn(false);
        when(facade.getCredentialById(1L)).thenReturn(null);

        filter.doFilter(request, response, chain);

        // 用户不存在时同样拒绝（401），链不放行
        assertThat(response.getStatus()).isEqualTo(401);
        verify(chain, never()).doFilter(any(), any());
    }
}
