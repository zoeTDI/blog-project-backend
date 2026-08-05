package cn.caldm.www.auth_context.infrastructure.repository;

import cn.caldm.www.auth_context.domain.repository.VerificationCodeRepository;
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
@Repository("authContextVerificationCodeRepositoryImpl")
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {

    private static final String CODE_PREFIX = "auth:code:email:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveCode(String target, String code, long ttlSeconds) {
        String key = CODE_PREFIX + target;
        stringRedisTemplate.opsForValue().set(key, code, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getCode(String target) {
        String key = CODE_PREFIX + target;
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteCode(String target) {
        String key = CODE_PREFIX + target;
        stringRedisTemplate.delete(key);
    }

    @Override
    public boolean verifyCode(String target, String code) {
        if (target == null || code == null) {
            return false;
        }
        String storedCode = getCode(target);
        if (storedCode != null && storedCode.equalsIgnoreCase(code.trim())) {
            deleteCode(target);
            return true;
        }
        return false;
    }
}
