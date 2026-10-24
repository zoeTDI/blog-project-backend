package cn.caldm.www.user_context.application.service;

import cn.caldm.www.shared_kernel.domain.ResourceNotFoundException;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.domain.model.*;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserRepository userRepository;
    private final UserNotificationFacadeService userNotificationFacadeService;
    private final UserRoleFacadeService userRoleFacadeService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String RESET_PWD_PREFIX = "user:reset:code:";

    public SysUser create(String username, String password, List<Long> roleIds) {
        Assert.notNull(roleIds, "The roles of new user must not be null.");

        List<UserRole> roles = userRoleFacadeService.getRolesByIds(roleIds);

        // 禁止创建管理员角色的用户
        if (roles.stream().anyMatch(role -> "ADMIN".equals(role.getCode()))) {
            throw new IllegalArgumentException("不能创建 ADMIN 角色的用户");
        }
        Long creatorId = currentUserProvider.getUserId();
        SysUser creator = userRepository.findById(creatorId);

        if (creator == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }

        if (!userRoleFacadeService.hasRole(creatorId, "ADMIN")) {
            throw new IllegalArgumentException("没有权限创建用户");
        }

        String encode = passwordEncoder.encode(password);
        List<UserRole> filtered = roles.stream().filter(Objects::nonNull).toList();
        SysUser inserted = userRepository.insert(creator.getUsername(), username, encode, filtered);
        if (inserted == null) {
            throw new IllegalStateException("创建用户失败");
        }
        return inserted;
    }

    public void ban(Long targetUserId) {
        Long updaterId = currentUserProvider.getUserId();
        SysUser updater = userRepository.findById(updaterId);

        if (updater == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }

        if (!userRoleFacadeService.hasRole(updaterId, "ADMIN")) {
            throw new IllegalArgumentException("没有权限封禁用户");
        }

        SysUser targetUser = userRepository.findById(targetUserId);

        if (targetUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }

        if (userRoleFacadeService.hasRole(targetUserId, "ADMIN")) {
            throw new IllegalArgumentException("管理员不可封禁");
        }

        if (targetUser.isDeleted()) {
            throw new IllegalArgumentException("目标用户已删除");
        }

        userRepository.updateStatus(targetUserId, SysUserStatusEnum.DISABLED, updater.getUsername());
    }

    public void softDelete(Long targetUserId) {
        Long updaterId = currentUserProvider.getUserId();
        SysUser updater = userRepository.findById(updaterId);
        if (updater == null) {
            throw new IllegalStateException("当前登录用户不存在");
        }
        SysUser targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标用户不存在");
        }
        // 已删除的用户不能再次删除
        if (targetUser.isDeleted()) {
            throw new IllegalArgumentException("目标用户已删除");
        }

        boolean updaterIsAdmin = userRoleFacadeService.hasRole(updaterId, "ADMIN");
        boolean targetIsAdmin = userRoleFacadeService.hasRole(targetUserId, "ADMIN");
        boolean isSelf = updaterId.equals(targetUserId);

        // 管理员不能删除管理员用户
        if (updaterIsAdmin && targetIsAdmin) {
            throw new IllegalArgumentException("管理员不可删除");
        }
        // 非管理员用户无权删除他人
        if (!updaterIsAdmin && !isSelf) {
            throw new IllegalArgumentException("没有权限删除用户");
        }

        userRepository.markDeleted(targetUserId, updater.getUsername());
    }

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
