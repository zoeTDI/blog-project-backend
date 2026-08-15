package cn.caldm.www.user_context.application.service;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.common.utils.SlowHashUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class UserApplicationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNotificationFacadeService userNotificationFacadeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String RESET_PWD_PREFIX = "user:reset:code:";

    public SysUser create(Long creatorId, String username, String password, List<RoleEnum> roles) {
        // 禁止创建管理员角色的用户
        for (RoleEnum role : roles) {
            if (RoleEnum.ADMIN.equalsRole(role)) {
                LogUtils.warn("不能创建 ADMIN 角色的用户");
                return null;
            }
        }
        SysUser creator = userRepository.findById(creatorId);
        boolean isAdmin = creator.hasRole(RoleEnum.ADMIN);
        // 不是管理员，不能创建用户
        if (!isAdmin) {
            LogUtils.warn("没有权限创建用户");
            return null;
        }
        return userRepository.insert(creator.getUsername(), username, password, roles);
    }

    public boolean ban(Long updaterId, Long targetUserId) {
        SysUser updater = userRepository.findById(updaterId);
        if (
                updater == null
                        || !updater.hasRole(RoleEnum.ADMIN)
        ) {
            return false;
        }

        SysUser targetUser = userRepository.findById(targetUserId);
        if (
                targetUser == null
                        || targetUser.hasRole(RoleEnum.ADMIN)
                        || targetUser.isDeleted()
        ) {
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

        boolean updaterIsAdmin = updater.hasRole(RoleEnum.ADMIN);
        boolean targetIsAdmin = targetUser.hasRole(RoleEnum.ADMIN);
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
        String encode = SlowHashUtils.bcryptEncode(newPassword);
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
