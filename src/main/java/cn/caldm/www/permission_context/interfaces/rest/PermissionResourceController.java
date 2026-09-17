package cn.caldm.www.permission_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.permission_context.application.service.PermissionService;
import cn.caldm.www.permission_context.domain.model.SystemResource;
import cn.caldm.www.permission_context.interfaces.assembler.ResourceDtoAssembler;
import cn.caldm.www.permission_context.interfaces.dto.resource.CreateResourceRequest;
import cn.caldm.www.permission_context.interfaces.dto.resource.ResourceResponse;
import cn.caldm.www.permission_context.interfaces.dto.resource.UpdateResourceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/permission/resources")
@RequiredArgsConstructor
public class PermissionResourceController {
    private final PermissionService permissionService;
    private final ResourceDtoAssembler resourceDtoAssembler;

    @PostMapping
    public Result<ResourceResponse> create(@Valid @RequestBody CreateResourceRequest request) {
        SystemResource resource = permissionService.createResource(
                request.getName(),
                request.getPermission(),
                request.getType(),
                request.getParentId(),
                request.getSort(),
                request.getPath(),
                request.getComponent(),
                request.getIcon(),
                request.getTitleKey()
        );
        return Result.success(resourceDtoAssembler.toResponse(resource));
    }

    @GetMapping("/{id}")
    public Result<ResourceResponse> getById(@PathVariable Long id) {
        SystemResource resource = permissionService.getResource(id);
        return Result.success(resourceDtoAssembler.toResponse(resource));
    }

    @GetMapping("/{id}/children")
    public Result<List<ResourceResponse>> getChildren(@PathVariable Long id) {
        List<SystemResource> resources = permissionService.getChildren(id);
        return Result.success(resourceDtoAssembler.toResponseList(resources));
    }

    @PutMapping("/{id}")
    public Result<ResourceResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateResourceRequest request) {
        SystemResource resource = permissionService.updateResource(
                id,
                request.getName(),
                request.getPermission(),
                request.getParentId(),
                request.getSort(),
                request.getPath(),
                request.getComponent(),
                request.getIcon(),
                request.getTitleKey()
        );
        return Result.success(resourceDtoAssembler.toResponse(resource));
    }

    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        permissionService.enableResource(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        permissionService.disableResource(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deleteResource(id);
        return Result.success();
    }
}
