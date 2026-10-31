package cn.caldm.www.auth_context.interfaces.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import cn.caldm.www.auth_context.infrastructure.web.AccessTokenFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.interfaces.Claim;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;

public class AccessTokenFilterTest {

    private final AuthUserFacadeService facade = mock(AuthUserFacadeService.class);
    private final JwtTokenProvider provider = mock(JwtTokenProvider.class);
    private final TokenBlacklistRepository blacklist = mock(TokenBlacklistRepository.class);
    private final AccessTokenFilter filter = new AccessTokenFilter(facade, provider, blacklist);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Map<String, Claim> validAccessClaims() {
        Claim type = mock(Claim.class);
        when(type.asString()).thenReturn("access");
        Claim id = mock(Claim.class);
        when(id.asLong()).thenReturn(1L);
        return Map.of("type", type, "id", id);
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));
        return request;
    }

    @Test
    void shouldNotAuthenticate_whenTokenBlacklisted() throws Exception {
        MockHttpServletRequest request = requestWithToken("token-1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Map<String, Claim> claims = validAccessClaims();
        when(provider.verifyToken("token-1")).thenReturn(claims);
        when(blacklist.isBlacklisted("token-1")).thenReturn(true);

        filter.doFilter(request, response, chain);

        // 黑名单 token 不得建立认证，也不该触达用户加载
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(facade, never()).getCredentialById(anyLong());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticate_whenTokenValidAndNotBlacklisted() throws Exception {
        MockHttpServletRequest request = requestWithToken("token-2");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 过滤器在 finally 中会 clearContext（安全实践），
        // 因此必须在链执行的那一刻捕获认证状态
        AtomicReference<Authentication> captured = new AtomicReference<>();
        FilterChain chain = (req, res) -> captured.set(SecurityContextHolder.getContext().getAuthentication());

        AuthUser authUser = new AuthUser();
        authUser.setId(1L);
        authUser.setUsername("admin");

        Map<String, Claim> claims = validAccessClaims();
        when(provider.verifyToken("token-2")).thenReturn(claims);
        when(blacklist.isBlacklisted("token-2")).thenReturn(false);
        when(facade.getCredentialById(1L)).thenReturn(authUser);

        filter.doFilter(request, response, chain);

        assertThat(captured.get()).isNotNull();
        assertThat(captured.get().getPrincipal()).isSameAs(authUser);
    }
}
