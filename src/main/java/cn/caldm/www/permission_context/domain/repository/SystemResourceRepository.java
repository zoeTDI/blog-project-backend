package cn.caldm.www.permission_context.domain.repository;

import cn.caldm.www.permission_context.domain.model.SystemResource;

import java.util.List;
import java.util.Optional;

public interface SystemResourceRepository {
    Optional<SystemResource> findById(Long id);

    List<SystemResource> findChildren(Long parentId);

    SystemResource create(SystemResource resource);

    SystemResource update(SystemResource resource);

    void deleteById(Long id);
}
