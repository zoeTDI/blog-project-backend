package cn.caldm.www.permission_context.application.service;

import cn.caldm.www.permission_context.domain.model.Role;

import java.util.Collection;
import java.util.Optional;

public interface RoleCacheService {
    void refresh();

    Optional<Role> findById(Long id);

    Optional<Role> findByCode(String code);

    Collection<Role> findAll();
}
