package cn.caldm.www.permission_context.interfaces.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.permission_context.application.service.RolePermissionService;
import cn.caldm.www.permission_context.domain.model.ResourceNode;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.security.SecurityUtils;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController 
@RequestMapping ("/permission")
@RequiredArgsConstructor 
public class RoleResourceController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/resource/{id}")
    @PreAuthorize ("@ss.isAdmin()")
    public Result<List<ResourceNode>> getMethodName(@PathVariable ("id") Long roleId) {
        return Result.success(rolePermissionService.getResourceByRoleId(roleId));
    }

    // @GetMapping("/resource/self")
    // @PreAuthorize ("@ss.hasRole()")
    // public Result<List<ResourceNode>> getSelfResource() {
    //     List<RoleEnum> roles = SecurityUtils.getRoles();
    //     if (roles == null) {
    //         return Result.success(new ArrayList<>());
    //     }
    //     // todo 修改数据表 采用角色名和权限名进行关联，而不是id
    //     return Result.success(new ArrayList<>());
    // }
    
    
}
