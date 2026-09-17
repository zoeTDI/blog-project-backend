package cn.caldm.www.permission_context.interfaces.assembler;

import cn.caldm.www.permission_context.domain.model.SystemResource;
import cn.caldm.www.permission_context.interfaces.dto.resource.ResourceResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class ResourceDtoAssembler {

    public ResourceResponse toResponse(SystemResource resource) {
        if (resource == null) {
            return null;
        }
        ResourceResponse response = new ResourceResponse();

        response.setId(resource.getId());
        response.setName(resource.getName());
        response.setPermission(resource.getPermission());
        response.setType(resource.getType());
        response.setParentId(resource.getParentId());
        response.setSort(resource.getSort());
        response.setPath(resource.getPath());
        response.setComponent(resource.getComponent());
        response.setIcon(resource.getIcon());
        response.setTitleKey(resource.getTitleKey());
        response.setEnabled(resource.isEnabled());

        return response;
    }

    public List<ResourceResponse> toResponseList(
            List<SystemResource> resources) {

        if (resources == null || resources.isEmpty()) {
            return List.of();
        }

        return resources.stream()
                .map(this::toResponse)
                .toList();
    }
}
