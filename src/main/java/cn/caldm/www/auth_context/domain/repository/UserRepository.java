package cn.caldm.www.auth_context.domain.repository;

import cn.caldm.www.auth_context.domain.model.AuthUser;

public interface UserRepository {
    AuthUser findByUsername(String username);
    AuthUser findById(Long id);
}
