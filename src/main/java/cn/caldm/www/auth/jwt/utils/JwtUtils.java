package cn.caldm.www.auth.jwt.utils;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.login.domain.SysUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * Jwt工具类
 *
 * @author caldm
 */
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationSeconds;

    private static String SECRET;
    private static long EXPIRATION;
    private static StringRedisTemplate STATIC_REDIS_TEMPLATE;

    private static final String TOKEN_BLACK_LIST_PREFIX = "auth:token:blacklist:";
    // private static final String TOKEN_BLACK_LIST_PREFIX = "Bearer ";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        SECRET = secretKey;
        EXPIRATION = expirationSeconds;
        STATIC_REDIS_TEMPLATE = redisTemplate;
    }

    public static String createToken(SysUser user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        return JWT.create()
                .withHeader(map)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static Map<String, Claim> verifyToken(String token) {
        try {
            if (isBlacklisted(token)) {
                LogUtils.error("Token 已在黑名单中（已登出或作废）");
                return null;
            }
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            LogUtils.error("token解码异常: " + e.getMessage());
            return null;
        }
    }

    public static void invalidateToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            long expireTime = expiresAt.getTime() - System.currentTimeMillis();
            if (expireTime > 0) {
                STATIC_REDIS_TEMPLATE.opsForValue().set(
                        TOKEN_BLACK_LIST_PREFIX + token,
                        "1",
                        expireTime,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            LogUtils.error("作废 Token 失败: " + e.getMessage());
        }
    }

    public static boolean isBlacklisted(String token) {
        if (STATIC_REDIS_TEMPLATE == null) {
            return false;
        }
        return Boolean.TRUE.equals(STATIC_REDIS_TEMPLATE.hasKey(TOKEN_BLACK_LIST_PREFIX + token));
    }
}
