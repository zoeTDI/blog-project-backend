package cn.caldm.www.user_context.domain.repository;

import cn.caldm.www.user_context.domain.modal.SysUser;

public interface UserRepository {
    SysUser findByUsername(String username);
    SysUser findById(Long id);
}
