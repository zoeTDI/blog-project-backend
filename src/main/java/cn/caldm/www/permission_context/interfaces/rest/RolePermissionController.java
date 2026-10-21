package cn.caldm.www.permission_context.interfaces.rest;

import cn.caldm.www.permission_context.application.service.RolePermissionService;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.shared_kernel.domain.Result;
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
@RequestMapping("/permission")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @GetMapping("/resource/{id}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<List<ResourceNode>> getResourcesByRoleId(@PathVariable("id") Long roleId) {
        return Result.success(rolePermissionService.getResourceByRoleId(roleId));
    }

    @PostMapping("/roles/{roleId}/resources/{resourceId}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> grantResourceToRole(@PathVariable("roleId") Long roleId,
                                            @PathVariable("resourceId") Long resourceId) {
        if (roleId == null || resourceId == null) {
            throw new IllegalArgumentException("非法参数");
        }
        rolePermissionService.grantResourceToRole(roleId, resourceId);
        return Result.success();
    }

    @DeleteMapping("/roles/{roleId}/resources/{resourceId}")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> revokeResourceFromRole(@PathVariable("roleId") Long roleId,
                                               @PathVariable("resourceId") Long resourceId) {
        if (roleId == null || resourceId == null) {
            throw new IllegalArgumentException("非法参数");
        }
        rolePermissionService.revokeResourceFromRole(roleId, resourceId);
        return Result.success();
    }
}
