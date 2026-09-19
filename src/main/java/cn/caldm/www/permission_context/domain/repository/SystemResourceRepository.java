package cn.caldm.www.permission_context.domain.repository;

import cn.caldm.www.permission_context.domain.model.Resource;

import java.util.List;
import java.util.Optional;

public interface SystemResourceRepository {
    Optional<Resource> findById(Long id);

    List<Resource> findByIds(List<Long> ids);

    List<Resource> findChildren(Long parentId);

    Resource create(Resource resource);

    Resource update(Resource resource);

    void deleteById(Long id);
}
