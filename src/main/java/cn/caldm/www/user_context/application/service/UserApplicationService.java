package cn.caldm.www.user_context.application.service;

import cn.caldm.www.shared_kernel.utils.LogUtils;
import cn.caldm.www.user_context.domain.model.*;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.time.LocalDateTime;
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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String RESET_PWD_PREFIX = "user:reset:code:";

    public SysUser create(Long creatorId, String username, String password, List<Long> roleIds) {
        Assert.notNull(roleIds, "The roles of new user must not be null.");

        List<UserRole> roles = userRoleFacadeService.getRolesByIds(roleIds);

        // 禁止创建管理员角色的用户
        if (roles.stream().anyMatch(role -> "ADMIN".equals(role.getCode()))) {
            LogUtils.warn("不能创建 ADMIN 角色的用户");
            return null;
        }

        SysUser creator = userRepository.findById(creatorId);

        if (creator == null || !userRoleFacadeService.hasRole(creatorId, "ADMIN")) {
            LogUtils.warn("没有权限创建用户");
            return null;
        }

        String encode = passwordEncoder.encode(password);
        List<UserRole> filtered = roles.stream().filter(Objects::nonNull).toList();
        return userRepository.insert(creator.getUsername(), username, encode, filtered);
    }

    public boolean ban(Long updaterId, Long targetUserId) {
        SysUser updater = userRepository.findById(updaterId);
        if (updater == null || !userRoleFacadeService.hasRole(updaterId, "ADMIN")) {
            return false;
        }

        SysUser targetUser = userRepository.findById(targetUserId);
        if (targetUser == null
                || userRoleFacadeService.hasRole(targetUserId, "ADMIN")
                || targetUser.isDeleted()) {
            return false;
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(targetUserId);
        sysUser.setStatus(SysUserStatusEnum.DISABLED);
        sysUser.setUpdater(updater.getUsername());
        sysUser.setUpdateTime(LocalDateTime.now());

        return userRepository.update(sysUser);
    }

    public boolean softDelete(Long updaterId, Long targetUserId) {
        SysUser updater = userRepository.findById(updaterId);
        if (updater == null) {
            return false;
        }
        SysUser targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            return false;
        }
        // 已删除的用户不能再次删除
        if (targetUser.isDeleted()) {
            return false;
        }

        boolean updaterIsAdmin = userRoleFacadeService.hasRole(updaterId, "ADMIN");
        boolean targetIsAdmin = userRoleFacadeService.hasRole(targetUserId, "ADMIN");
        boolean isSelf = updaterId.equals(targetUserId);

        // 管理员不能删除管理员用户
        if (updaterIsAdmin && targetIsAdmin) {
            return false;
        }
        // 非管理员用户无权删除他人
        if (!updaterIsAdmin && !isSelf) {
            return false;
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(targetUserId);
        sysUser.setDeleted(SysUserDeletedEnum.DELETED);
        sysUser.setUpdater(updater.getUsername());
        sysUser.setUpdateTime(LocalDateTime.now());

        return userRepository.update(sysUser);
    }

    public boolean sendPasswordResetEmail(Long userId) {
        SysUser sysUser = userRepository.findById(userId);
        if (sysUser == null) {
            return false;
        }
        String email = sysUser.getEmail();
        String code = String.format("%06d", new SecureRandom().nextInt(900000) + 100000);

        try {
            userNotificationFacadeService.sendPasswordResetCode(email, code);
        } catch (Exception e) {
            LogUtils.error("用户「" + userId + "」密码重置邮件发送失败: " + e.getMessage());
            return false;
        }
        stringRedisTemplate.opsForValue().set(RESET_PWD_PREFIX + email, code, 5, TimeUnit.MINUTES);
        return true;
    }

    public boolean resetPassword(Long targetUserId, String code, String newPassword) {
        SysUser sysUser = userRepository.findById(targetUserId);
        if (sysUser == null) {
            return false;
        }
        if (sysUser.isDisabled() || sysUser.isDeleted()) {
            LogUtils.warn("用户「" + targetUserId + "」状态异常，无法重置密码");
            return false;
        }
        String redisKey = RESET_PWD_PREFIX + sysUser.getEmail();
        String tokenCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (tokenCode == null || !tokenCode.equals(code)) {
            return false;
        }

        SysUser updateUser = new SysUser();
        String encode = passwordEncoder.encode(newPassword);
        updateUser.setId(targetUserId);
        updateUser.setPassword(encode);
        updateUser.setUpdater(sysUser.getUpdater());
        updateUser.setUpdateTime(LocalDateTime.now());

        boolean updated = userRepository.update(updateUser);
        if (updated) {
            stringRedisTemplate.delete(redisKey);
        }
        return updated;
    }
}
