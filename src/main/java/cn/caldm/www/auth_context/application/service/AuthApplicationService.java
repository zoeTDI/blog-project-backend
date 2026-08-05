package cn.caldm.www.auth_context.application.service;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.TokenPair;
import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import cn.caldm.www.auth_context.domain.repository.UserRepository;
import cn.caldm.www.auth_context.infrastructure.security.JwtTokenProvider;
import cn.caldm.www.common.utils.SlowHashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 登录状态编排
 *
 * @author caldm
 */
@Service
public class AuthApplicationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenBlacklistRepository blacklistRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TokenPair login(String username, String password) {
        AuthUser user = userRepository.findByUsername(username);

        if (user == null || !SlowHashUtils.bcryptMatches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 已封禁或已删除用户不允许登录
        if (user.getStatus() == 1 || user.getDeleted()) {
            return null;
        }
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);
        return new TokenPair(accessToken, refreshToken);
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
        AuthUser user = userRepository.findById(id);
        // 已封禁或已删除用户不允许刷新 token
        if (user.getStatus() == 1 || user.getDeleted()) {
            return null;
        }
        blacklistRepository.addBlacklist(oldRefreshToken, jwtTokenProvider.getRemainingExpiration(oldRefreshToken));
        return new TokenPair(
                jwtTokenProvider.createAccessToken(user),
                jwtTokenProvider.createRefreshToken(user)
        );
    }
}
