package cn.caldm.www.auth_context.domain.repository;

import cn.caldm.www.auth_context.domain.model.SysUser;

public interface UserRepository {
    SysUser findByUsername(String username);
    SysUser findById(Long id);
}
