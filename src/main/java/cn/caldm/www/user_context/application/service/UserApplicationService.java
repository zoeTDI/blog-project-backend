package cn.caldm.www.user_context.application.service;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}
