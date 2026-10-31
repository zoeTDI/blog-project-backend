package cn.caldm.www.auth_context.application.service;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import cn.caldm.www.auth_context.domain.repository.VerificationCodeRepository;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.auth_context.interfaces.dto.LoginECCommand;
import cn.caldm.www.auth_context.interfaces.dto.LoginEPCommand;
import cn.caldm.www.auth_context.interfaces.dto.LoginUPCommand;
import cn.caldm.www.auth_context.interfaces.dto.SendLoginCodeCommand;
import cn.caldm.www.shared_kernel.utils.LogUtils;
import cn.caldm.www.user_context.domain.model.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;
import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.SecureRandom;
import java.util.Map;

/**
 *
 * 登录状态编排
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthUserFacadeService authUserFacadeService;
    private final AuthNotificationFacadeService notificationFacadeService;
    private final TokenBlacklistRepository blacklistRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private static final String LOGIN_PREFIX = "user:login:code:";
    /** 过期时间统一提前 1 分钟，防止服务端/客户端时钟偏差导致 token 刚下发即被判过期 */
    private static final long TOKEN_GRACE_MILLIS = 60_000L;

    public AuthUser loginByUP(@Validated LoginUPCommand command) {
        String username = command.getUsername();
        String password = command.getPassword();
        AuthUser user = authUserFacadeService.getCredentialByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        applyTokenPair(user, generateTokenForUser(user));
        return user;
    }

    public AuthUser loginByEP(@Validated LoginEPCommand command) {
        String email = command.getEmail();
        String password = command.getPassword();
        AuthUser user = authUserFacadeService.getCredentialByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("邮箱或密码错误");
        }
        applyTokenPair(user, generateTokenForUser(user));
        return user;
    }

    public AuthUser loginByEC(@Validated LoginECCommand command) {
        String email = command.getEmail();
        String code = command.getCode();
        String key = LOGIN_PREFIX + email;
        boolean isValid = verificationCodeRepository.verifyCode(key, code);
        if (!isValid) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        AuthUser user = authUserFacadeService.getCredentialByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        applyTokenPair(user, generateTokenForUser(user));
        return user;
    }

    /**
     * 发送登录验证码
     */
    public boolean sendLoginCode(@Validated SendLoginCodeCommand command) {
        String email = command.getEmail();

        if (!verificationCodeRepository.tryMarkSendCooldown(email, 60)) {
            throw new IllegalArgumentException("发送过于频繁，请稍后再试");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(900000) + 100000);

        try {
            notificationFacadeService.sendLoginCode(email, code);
        } catch (Exception e) {
            LogUtils.error("邮箱「" + email + "」登录验证码发送失败: " + e.getMessage());
            return false;
        }

        String key = LOGIN_PREFIX + email;
        verificationCodeRepository.saveCode(key, code, 5*60);
        return true;
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            long expire = jwtTokenProvider.getRemainingExpiration(accessToken);
            blacklistRepository.addBlacklist(accessToken, expire);
        }
        if (refreshToken != null && !refreshToken.isEmpty()) {
            long expire = jwtTokenProvider.getRemainingExpiration(refreshToken);
            blacklistRepository.addBlacklist(refreshToken, expire);
        }
    }

    public TokenPair refreshToken(String oldRefreshToken) {
        var claims = jwtTokenProvider.verifyToken(oldRefreshToken);
        if (claims == null || !"refresh".equals(claims.get("type").asString())) {
            throw new IllegalArgumentException("非法的 Refresh Token");
        }

        Long id = claims.get("id").asLong();
        AuthUser user = authUserFacadeService.getCredentialById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 已封禁或已删除用户不允许刷新 token
        if (SysUserStatusEnum.DISABLED.equals(user.getStatus())
            || SysUserDeletedEnum.DELETED.equals(user.getDeleted())
        ) {
            throw new IllegalArgumentException("账号已被封禁或注销");
        }
        long remaining = jwtTokenProvider.getRemainingExpiration(oldRefreshToken);
        if (remaining <= 0 || !blacklistRepository.tryBlacklist(oldRefreshToken, remaining)) {
            throw new IllegalArgumentException("Refresh Token 已失效");
        }
        return buildTokenPair(user);
    }


    /**
     * 将 TokenPair 写回 AuthUser，供登录接口返回。
     */
    private void applyTokenPair(AuthUser user, TokenPair tokenPair) {
        user.setAccessToken(tokenPair.getAccessToken());
        user.setRefreshToken(tokenPair.getRefreshToken());
        user.setAccessTokenExpiresAt(tokenPair.getAccessTokenExpiresAt());
        user.setRefreshTokenExpiresAt(tokenPair.getRefreshTokenExpiresAt());
    }

    /**
     * 构造 TokenPair，不含用户状态校验（调用方负责）。
     * 过期时间统一减去 TOKEN_GRACE_MILLIS。
     */
    private TokenPair buildTokenPair(AuthUser user) {
        long now = System.currentTimeMillis();
        return new TokenPair(
                jwtTokenProvider.createAccessToken(user),
                jwtTokenProvider.createRefreshToken(user),
                now + (jwtTokenProvider.getAccessTokenExpirationSeconds() * 1000) - TOKEN_GRACE_MILLIS,
                now + (jwtTokenProvider.getRefreshTokenExpirationSeconds() * 1000) - TOKEN_GRACE_MILLIS
        );
    }

    private TokenPair generateTokenForUser(AuthUser user) {
        if (SysUserStatusEnum.DISABLED.equals(user.getStatus())
                || SysUserDeletedEnum.DELETED.equals(user.getDeleted())
        ) {
            throw new IllegalArgumentException("账号已被封禁或注销");
        }
        return buildTokenPair(user);
    }
}
