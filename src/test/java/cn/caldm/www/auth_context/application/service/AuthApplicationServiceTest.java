package cn.caldm.www.auth_context.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth0.jwt.interfaces.Claim;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import cn.caldm.www.auth_context.domain.repository.VerificationCodeRepository;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.auth_context.interfaces.dto.LoginUPCommand;
import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;

@ExtendWith(MockitoExtension.class)
public class AuthApplicationServiceTest {

    @Mock
    private AuthUserFacadeService authUserFacadeService;
    @Mock
    private AuthNotificationFacadeService notificationFacadeService;
    @Mock
    private TokenBlacklistRepository blacklistRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthApplicationService authApplicationService;

    @Test
    void loginByUP_shouldThrowOnWrongPassword() {
        AuthUser user = new AuthUser();
        user.setUsername("admin");
        user.setPassword("{bcrypt}$2a$10$hash");

        when(authUserFacadeService.getCredentialByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        LoginUPCommand command = new LoginUPCommand();
        command.setUsername("admin");
        command.setPassword("wrong");

        // 密码错误必须是 400 语义且消息透传（而非 500 兜底吞消息）
        assertThatThrownBy(() -> authApplicationService.loginByUP(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void refreshToken_shouldThrowWhenUserDisabled() {
        Map<String, Claim> claims = refreshClaims();
        when(blacklistRepository.isBlacklisted("rt")).thenReturn(false);
        when(jwtTokenProvider.verifyToken("rt")).thenReturn(claims);

        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setStatus(SysUserStatusEnum.DISABLED);
        when(authUserFacadeService.getCredentialById(1L)).thenReturn(user);

        assertThatThrownBy(() -> authApplicationService.refreshToken("rt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("封禁");
    }

    @Test
    void refreshToken_shouldThrowWhenUserNotFound() {
        Map<String, Claim> claims = refreshClaims();
        when(blacklistRepository.isBlacklisted("rt")).thenReturn(false);
        when(jwtTokenProvider.verifyToken("rt")).thenReturn(claims);
        when(authUserFacadeService.getCredentialById(1L)).thenReturn(null);

        assertThatThrownBy(() -> authApplicationService.refreshToken("rt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户不存在");
    }

    private Map<String, Claim> refreshClaims() {
        Claim type = mock(Claim.class);
        when(type.asString()).thenReturn("refresh");
        Claim id = mock(Claim.class);
        when(id.asLong()).thenReturn(1L);
        return Map.of("type", type, "id", id);
    }
}
