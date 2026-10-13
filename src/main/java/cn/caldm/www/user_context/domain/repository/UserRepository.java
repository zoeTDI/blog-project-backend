package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.modal.UserRole;

import java.util.List;

public interface UserRepository {
    SysUser findByEmail(String email);
    SysUser findByUsername(String username);
    SysUser findById(Long id);
    SysUser insert(String creator, String username, String password, List<UserRole> roles);
    boolean update(SysUser sysUser);
}
