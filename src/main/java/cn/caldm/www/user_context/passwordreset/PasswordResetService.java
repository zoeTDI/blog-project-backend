package cn.caldm.www.user_context.passwordreset;

import cn.caldm.www.shared_kernel.domain.ResourceNotFoundException;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.ports.UserNotificationFacadeService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 *
 * 密码重置用例：发送验证码邮件、校验验证码并重置密码
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final UserNotificationFacadeService userNotificationFacadeService;
    private final PasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String RESET_PWD_PREFIX = "user:reset:code:";

    public void sendPasswordResetEmail(Long userId) {
        SysUser sysUser = userRepository.findById(userId);
        if (sysUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }
        String email = sysUser.getEmail();
        String code = String.format("%06d", new SecureRandom().nextInt(900000) + 100000);

        userNotificationFacadeService.sendPasswordResetCode(email, code);

        stringRedisTemplate.opsForValue().set(RESET_PWD_PREFIX + email, code, 5, TimeUnit.MINUTES);
    }

    public void resetPassword(Long targetUserId, String code, String newPassword) {
        SysUser sysUser = userRepository.findById(targetUserId);
        if (sysUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }

        if (sysUser.isDisabled() || sysUser.isDeleted()) {
            throw new IllegalArgumentException("用户状态异常，无法重置密码");
        }

        String redisKey = RESET_PWD_PREFIX + sysUser.getEmail();
        String tokenCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (tokenCode == null || !tokenCode.equals(code)) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }

        String encode = passwordEncoder.encode(newPassword);
        boolean updated = userRepository.updatePassword(targetUserId, encode, sysUser.getUsername());
        if (updated) {
            stringRedisTemplate.delete(redisKey);
        }
    }
}
