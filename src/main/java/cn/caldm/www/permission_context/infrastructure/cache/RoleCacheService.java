package cn.caldm.www.permission_context.infrastructure.cache;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@AllArgsConstructor
public class RoleCacheService implements cn.caldm.www.permission_context.application.service.RoleCacheService {

    private final RoleRepository roleRepository;
    private final AtomicReference<RoleSnapshot> snapshot = new AtomicReference<>(RoleSnapshot.empty());

    @Override
    public void refresh() {
        snapshot.set(RoleSnapshot.from(roleRepository.getAll()));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(snapshot.get().byId().get(id));
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return Optional.ofNullable(snapshot.get().byCode().get(code));
    }

    @Override
    public Collection<Role> findAll() {
        return snapshot.get().byId().values();
    }

}
