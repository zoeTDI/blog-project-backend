package cn.caldm.www.permission_context.application.service;

import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.domain.model.SystemResource;

import java.util.List;

public interface PermissionService {

    SystemResource createResource(
            String name,
            String permission,
            ResourceTypeEnum type,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey
    );

    SystemResource getResource(Long id);

    List<SystemResource> getChildren(Long parentId);

    SystemResource updateResource(
            Long id,
            String name,
            String permission,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey
    );

    void enableResource(Long id);

    void disableResource(Long id);

    void deleteResource(Long id);
}
