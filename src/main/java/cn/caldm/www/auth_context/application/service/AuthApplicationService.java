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
import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.common.utils.SlowHashUtils;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.SecureRandom;

/**
 *
 * 登录状态编排
 *
 * @author caldm
 */
@Service
public class AuthApplicationService {
    @Autowired
    private AuthUserFacadeService authUserFacadeService;
    @Autowired
    private AuthNotificationFacadeService notificationFacadeService;
    @Autowired
    private TokenBlacklistRepository blacklistRepository;
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final String LOGIN_PREFIX = "user:login:code:";

    public AuthUser loginByUP(@Validated LoginUPCommand command) {
        String username = command.getUsername();
        String password = command.getPassword();
        AuthUser user = authUserFacadeService.getCredentialByUsername(username);

        if (user == null || !SlowHashUtils.bcryptMatches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        TokenPair tokenPair = generateTokenForUser(user);
        user.setRefreshToken(tokenPair.getRefreshToken());
        user.setAccessToken(tokenPair.getAccessToken());
        user.setAccessTokenExpiresAt(tokenPair.getAccessTokenExpiresAt());
        user.setRefreshTokenExpiresAt(tokenPair.getRefreshTokenExpiresAt());
        return user;
    }

    public AuthUser loginByEP(@Validated LoginEPCommand command) {
        String email = command.getEmail();
        String password = command.getPassword();
        AuthUser user = authUserFacadeService.getCredentialByEmail(email);
        if (user == null || !SlowHashUtils.bcryptMatches(password, user.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }
        TokenPair tokenPair = generateTokenForUser(user);
        user.setRefreshToken(tokenPair.getRefreshToken());
        user.setAccessToken(tokenPair.getAccessToken());
        user.setAccessTokenExpiresAt(tokenPair.getAccessTokenExpiresAt());
        user.setRefreshTokenExpiresAt(tokenPair.getRefreshTokenExpiresAt());
        return user;
    }

    public AuthUser loginByEC(@Validated LoginECCommand command) {
        String email = command.getEmail();
        String code = command.getCode();
        String key = LOGIN_PREFIX + email;
        boolean isValid = verificationCodeRepository.verifyCode(key, code);
        if (!isValid) {
            throw new RuntimeException("验证码错误或已过期");
        }
        AuthUser user = authUserFacadeService.getCredentialByEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱尚未注册");
        }
        TokenPair tokenPair = generateTokenForUser(user);
        user.setRefreshToken(tokenPair.getRefreshToken());
        user.setAccessToken(tokenPair.getAccessToken());
        user.setAccessTokenExpiresAt(tokenPair.getAccessTokenExpiresAt());
        user.setRefreshTokenExpiresAt(tokenPair.getRefreshTokenExpiresAt());
        return user;
    }

    /**
     * 发送登录验证码
     */
    public boolean sendLoginCode(@Validated SendLoginCodeCommand command) {
        String email = command.getEmail();
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
        if (blacklistRepository.isBlacklisted(oldRefreshToken)) {
            throw new RuntimeException("Refresh Token 已失效");
        }
        var claims = jwtTokenProvider.verifyToken(oldRefreshToken);
        if (claims == null || !"refresh".equals(claims.get("type").asString())) {
            throw new RuntimeException("非法的 Refresh Token");
        }

        Long id = claims.get("id").asLong();
        AuthUser user = authUserFacadeService.getCredentialById(id);
        // 已封禁或已删除用户不允许刷新 token
        if (SysUserStatusEnum.DISABLED.equals(user.getStatus())
            || SysUserDeletedEnum.DELETED.equals(user.getDeleted())
        ) {
            return null;
        }
        blacklistRepository.addBlacklist(oldRefreshToken, jwtTokenProvider.getRemainingExpiration(oldRefreshToken));
        long now = System.currentTimeMillis();
        return new TokenPair(
                jwtTokenProvider.createAccessToken(user),
                jwtTokenProvider.createRefreshToken(user),
                now + (jwtTokenProvider.getAccessTokenExpirationSeconds() * 1000) - 60000,
                now + (jwtTokenProvider.getRefreshTokenExpirationSeconds() * 1000) - 60000
        );
    }

    private TokenPair generateTokenForUser(AuthUser user) {
        if (SysUserStatusEnum.DISABLED.equals(user.getStatus())
                || SysUserDeletedEnum.DELETED.equals(user.getDeleted())
        ) {
            throw new RuntimeException("账号已被封禁或注销");
        }
        long now = System.currentTimeMillis();
        return new TokenPair(
                jwtTokenProvider.createAccessToken(user),
                jwtTokenProvider.createRefreshToken(user),
                now + (jwtTokenProvider.getAccessTokenExpirationSeconds() * 1000) - 60000,
                now + (jwtTokenProvider.getRefreshTokenExpirationSeconds() * 1000) - 60000
        );
    }
}
