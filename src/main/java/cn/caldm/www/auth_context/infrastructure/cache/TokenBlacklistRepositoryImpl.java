package cn.caldm.www.auth_context.infrastructure.cache;

import cn.caldm.www.auth_context.domain.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
public class TokenBlacklistRepositoryImpl implements TokenBlacklistRepository {
    private static final String PREFIX = "auth:token:blacklist:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void addBlacklist(String token, long expireMillis) {
        if (expireMillis > 0) {
            redisTemplate.opsForValue().set(PREFIX + token, "1", expireMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
