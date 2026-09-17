package cn.caldm.www.permission_context.application.service;

import cn.caldm.www.permission_context.domain.model.ResourceTypeEnum;
import cn.caldm.www.permission_context.domain.model.SystemResource;
import cn.caldm.www.permission_context.domain.repository.SystemResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SystemResourceRepository resourceRepository;

    @Override
    public SystemResource createResource(
            String name,
            String permission,
            ResourceTypeEnum type,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey) {

        SystemResource parent = null;

        if (!Long.valueOf(0L).equals(parentId)) {
            parent = resourceRepository.findById(parentId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "父资源不存在：" + parentId
                            ));
        }

        SystemResource resource = SystemResource.create(
                name,
                permission,
                type,
                parentId,
                sort,
                path,
                component,
                icon,
                titleKey
        );

        resource.validateParent(parent);

        return resourceRepository.create(resource);
    }

    @Override
    public SystemResource getResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("资源不存在：" + id));
    }

    @Override
    public List<SystemResource> getChildren(Long parentId) {
        getResource(parentId);
        return  resourceRepository.findChildren(parentId);
    }

    @Override
    public SystemResource updateResource(
            Long id,
            String name,
            String permission,
            Long parentId,
            Integer sort,
            String path,
            String component,
            String icon,
            String titleKey) {

        SystemResource resource = getResource(id);

        SystemResource parent = null;

        if (!Long.valueOf(0L).equals(parentId)) {
            parent = resourceRepository.findById(parentId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "父资源不存在：" + parentId
                            ));
        }

        if (parent != null) {
            resource.validateParent(parent);
        } else if (!resource.isDirectory()) {
            throw new IllegalArgumentException("顶级资源必须是目录。");
        }

        resource.update(
                name,
                permission,
                parentId,
                sort,
                path,
                component,
                icon,
                titleKey
        );

        return resourceRepository.update(resource);
    }

    @Override
    public void enableResource(Long id) {
        SystemResource resource = getResource(id);

        resource.enable();

        resourceRepository.update(resource);
    }

    @Override
    public void disableResource(Long id) {
        SystemResource resource = getResource(id);

        resource.disable();

        resourceRepository.update(resource);
    }

    @Override
    public void deleteResource(Long id) {
        SystemResource resource = getResource(id);

        List<SystemResource> children =
                resourceRepository.findChildren(id);

        if (!children.isEmpty()) {
            throw new IllegalArgumentException(
                    "资源存在子资源，无法删除：" + resource.getName()
            );
        }

        resourceRepository.deleteById(id);
    }
}
