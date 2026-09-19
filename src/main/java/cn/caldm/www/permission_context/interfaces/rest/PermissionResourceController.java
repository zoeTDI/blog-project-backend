package cn.caldm.www.permission_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.permission_context.application.service.PermissionService;
import cn.caldm.www.permission_context.domain.model.Resource;
import cn.caldm.www.permission_context.interfaces.assembler.ResourceDtoAssembler;
import cn.caldm.www.permission_context.interfaces.dto.resource.CreateResourceRequest;
import cn.caldm.www.permission_context.interfaces.dto.resource.ResourceResponse;
import cn.caldm.www.permission_context.interfaces.dto.resource.UpdateResourceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize ("@ss.isAdmin()")
    public Result<ResourceResponse> create(@Valid @RequestBody CreateResourceRequest request) {
        Resource resource = permissionService.createResource(
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
    @PreAuthorize ("@ss.hasRole()")
    public Result<ResourceResponse> getById(@PathVariable Long id) {
        Resource resource = permissionService.getResource(id);
        return Result.success(resourceDtoAssembler.toResponse(resource));
    }

    @GetMapping("/{id}/children")
    @PreAuthorize ("@ss.hasRole()")
    public Result<List<ResourceResponse>> getChildren(@PathVariable Long id) {
        List<Resource> resources = permissionService.getChildren(id);
        return Result.success(resourceDtoAssembler.toResponseList(resources));
    }

    @PutMapping("/{id}")
    @PreAuthorize ("@ss.hasRole()")
    public Result<ResourceResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateResourceRequest request) {
        Resource resource = permissionService.updateResource(
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
    @PreAuthorize ("@ss.isAdmin()")
    public Result<Void> enable(@PathVariable Long id) {
        permissionService.enableResource(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize ("@ss.isAdmin()")
    public Result<Void> disable(@PathVariable Long id) {
        permissionService.disableResource(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize ("@ss.isAdmin()")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deleteResource(id);
        return Result.success();
    }
}
