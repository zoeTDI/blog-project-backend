package cn.caldm.www.permission_context.interfaces.rest;

import cn.caldm.www.permission_context.application.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import cn.caldm.www.permission_context.application.service.RolePermissionService;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.shared_kernel.domain.Result;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class RoleResourceController {

    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    @GetMapping("/resource/{id}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<List<ResourceNode>> getMethodName(@PathVariable("id") Long roleId) {
        return Result.success(rolePermissionService.getResourceByRoleId(roleId));
    }

    @PostMapping("/roles/{roleId}/resources/{resourceId}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> grantResourceToRole(@PathVariable("roleId") Long roleId, @PathVariable("resourceId") Long resourceId) {
        if (roleId == null || resourceId == null) {
            throw new IllegalArgumentException("非法参数");
        }
        permissionService.grantResourceToRole(roleId, resourceId);
        return Result.success();
    }

    @DeleteMapping("/roles/{roleId}/resources/{resourceId}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> revokeResourceFromRole(@PathVariable("roleId") Long roleId, @PathVariable("resourceId") Long resourceId) {
        if (roleId == null || resourceId == null) {
            throw new IllegalArgumentException("非法参数");
        }
        permissionService.revokeResourceFromRole(roleId, resourceId);
        return Result.success();
    }
}
