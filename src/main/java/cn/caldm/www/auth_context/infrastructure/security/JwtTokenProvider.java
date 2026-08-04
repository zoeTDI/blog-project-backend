package cn.caldm.www.auth_context.infrastructure.security;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600}")
    private long accessTokenExpirationSeconds;

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshTokenExpirationSeconds;

    public String createAccessToken(AuthUser user) {
        return createToken(user, accessTokenExpirationSeconds, "access");
    }

    public String createRefreshToken(AuthUser user) {
        return createToken(user, refreshTokenExpirationSeconds, "refresh");
    }

    private String createToken(AuthUser user, long expireSeconds, String tokenType) {
        Date expireDate = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        return JWT.create()
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("type", tokenType)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Map<String, Claim> verifyToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }

    public long getRemainingExpiration(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
}
