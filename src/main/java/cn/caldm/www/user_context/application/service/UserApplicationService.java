package cn.caldm.www.user_context.application.service;

import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SysUser create(Long creatorId, String role, String username, String password) {
        SysUser creator = userRepository.findById(creatorId);
        // todo 后续对接权限校验
        SysUser newUser = userRepository.insert(creator.getUsername(), username, password);
        // todo 为账号关联角色 role
        return newUser;
    }
}
