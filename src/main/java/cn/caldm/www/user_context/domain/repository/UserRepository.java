package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;

import java.util.List;

public interface UserRepository {
    SysUser findByUsername(String username);
    SysUser findById(Long id);
    SysUser insert(String creator, String username, String password, List<RoleEnum> roles);
}
