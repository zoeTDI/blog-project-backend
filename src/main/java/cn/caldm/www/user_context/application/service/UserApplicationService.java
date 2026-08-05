package cn.caldm.www.user_context.application.service;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.common.utils.SlowHashUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.email.EmailSender;
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

    @Resource
    private EmailSender emailSender;

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
        // 不是管理员，不允许进行该操作
        if (!updater.hasRole(RoleEnum.ADMIN)) {
            return false;
        }
        SysUser targetUser = userRepository.findById(targetUserId);
        // 不允许封禁管理员
        if (targetUser.hasRole(RoleEnum.ADMIN)) {
            return false;
        }
        // 不允许封禁软删除用户
        if (targetUser.getDeleted()) {
            return false;
        }

        SysUserPO updatePo = new SysUserPO();
        updatePo.setId(targetUserId);
        updatePo.setStatus((short) 1);
        updatePo.setUpdater(updater.getUsername());
        updatePo.setUpdateTime(LocalDateTime.now());
        return userRepository.update(updatePo);
    }

    public boolean softDelete(Long updaterId, Long targetUserId) {
        SysUser updater = userRepository.findById(updaterId);
        if (updater == null) {
            return false;
        }
        // 操作者不是管理员或者自身，不允许删除
        if (updater.hasRole(RoleEnum.ADMIN) || !updaterId.equals(targetUserId)) {
            return false;
        }
        SysUser targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            return false;
        }
        // 不允许删除管理员
        if (targetUser.hasRole(RoleEnum.ADMIN)) {
            return false;
        }

        SysUserPO updatePo = new SysUserPO();
        updatePo.setId(targetUserId);
        updatePo.setDeleted(true);
        updatePo.setUpdater(updater.getUsername());
        updatePo.setUpdateTime(LocalDateTime.now());
        return userRepository.update(updatePo);
    }

    public boolean sendPasswordRestEmail(Long userId) {
        SysUser sysUser = userRepository.findById(userId);
        if (sysUser == null) {
            return false;
        }
        String email = sysUser.getEmail();
        String code = String.format("%06d", new SecureRandom().nextInt(900000) + 100000);

        try {
            emailSender.sendVerificationCode(email, code);
        } catch (Exception e) {
            LogUtils.error("用户「" + userId + "」密码重置邮件发送失败: " + e.getMessage());
            return false;
        }
        stringRedisTemplate.opsForValue().set(RESET_PWD_PREFIX+email, code, 5, TimeUnit.MINUTES);
        return true;
    }

    public boolean resetPassword(Long targetUserId, String code, String newPassword) {
        SysUser sysUser = userRepository.findById(targetUserId);
        if (sysUser == null) {
            return false;
        }
        if (sysUser.getStatus() == (short) 1 || sysUser.getDeleted()) {
            LogUtils.warn("用户「" + targetUserId + "」状态异常，无法重置密码");
            return false;
        }
        String redisKey = RESET_PWD_PREFIX + sysUser.getEmail();
        String tokenCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (tokenCode == null || !tokenCode.equals(code)) {
            return false;
        }

        SysUserPO po = new SysUserPO();
        String encode = SlowHashUtils.bcryptEncode(newPassword);
        po.setId(targetUserId);
        po.setPassword(encode);
        po.setUpdater(sysUser.getUsername());
        po.setUpdateTime(LocalDateTime.now());

        boolean updated = userRepository.update(po);
        if (updated) {
            stringRedisTemplate.delete(redisKey);
        }
        return updated;
    }
}
