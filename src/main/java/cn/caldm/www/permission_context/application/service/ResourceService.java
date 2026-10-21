package cn.caldm.www.permission_context.application.service;

import cn.caldm.www.permission_context.application.command.CreateResourceCommand;
import cn.caldm.www.permission_context.application.command.UpdateResourceCommand;
import cn.caldm.www.permission_context.domain.model.Resource;

import java.util.List;

public interface ResourceService {
    Resource createResource(CreateResourceCommand command);

    Resource getResource(Long id);

    List<Resource> getChildren(Long parentId);

    Resource updateResource(UpdateResourceCommand command);

    void enableResource(Long id);

    void disableResource(Long id);

    void deleteResource(Long id);
}
