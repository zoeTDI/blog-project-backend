package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.domain.model.SysUserStatusEnum;
import cn.caldm.www.user_context.domain.model.UserRole;

import java.util.List;

public interface UserRepository {
    SysUser findByEmail(String email);
    SysUser findByUsername(String username);
    SysUser findById(Long id);
    SysUser insert(String creator, String username, String password, List<UserRole> roles);
    boolean updateStatus(Long id, SysUserStatusEnum status, String updater);
    boolean markDeleted(Long id, String updater);
    boolean updatePassword(Long id, String encodedPassword, String updater);
}
