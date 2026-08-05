package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.infrastructure.persistence.po.SysUserPO;

import java.util.List;

public interface UserRepository {
    SysUser findByEmail(String email);
    SysUser findByUsername(String username);
    SysUser findById(Long id);
    SysUser insert(String creator, String username, String password, List<RoleEnum> roles);
    boolean update(SysUserPO sysUserPO);
}
