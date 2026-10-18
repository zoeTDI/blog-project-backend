package cn.caldm.www.permission_context.application.service;

import cn.caldm.www.permission_context.application.command.CreateResourceCommand;
import cn.caldm.www.permission_context.domain.model.Resource;

import java.util.List;

public interface PermissionService {

    Resource createResource(CreateResourceCommand command);

    Resource getResource(Long id);

    List<Resource> getChildren(Long parentId);

    Resource updateResource(
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

    void grantResourceToRole(Long roleId, Long resourceId);

    void revokeResourceFromRole(Long roleId, Long resourceId);

}
