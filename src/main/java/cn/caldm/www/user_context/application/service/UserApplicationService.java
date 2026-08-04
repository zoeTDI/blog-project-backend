package cn.caldm.www.user_context.application.service;

import cn.caldm.www.common.utils.LogUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
