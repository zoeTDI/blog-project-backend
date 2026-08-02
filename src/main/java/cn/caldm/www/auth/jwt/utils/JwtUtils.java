package cn.caldm.www.auth.jwt.utils;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.login.domain.SysUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Jwt工具类
 *
 * @author caldm
 */
public class JwtUtils {
    /**
     * 密钥
     */
    private static final String SECRET = "my_secret";

    /**
     * 过期时间（单位：秒）
     */
    private static final long EXPIRATION = 1800L;

    public static String createToken(SysUser user) {
        Date expirDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return JWT.create()
                .withHeader(map)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("password", user.getPassword())
                // 设置过期时间
                .withExpiresAt(expirDate)
                // 设置签发时间
                .withIssuedAt(new Date())
                // 加密
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            LogUtils.error("token解码异常");
            LogUtils.error(e.getMessage());
            return null;
        }
        return jwt.getClaims();
    }
}
