package cn.caldm.www.permission_context.interfaces.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.permission_context.application.service.UserPermissionService;
import cn.caldm.www.permission_context.interfaces.assembler.MenuDtoAssembler;
import cn.caldm.www.permission_context.interfaces.menu.MenuVO;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/permission/me")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;
    private final CurrentUserProvider currentUserProvider;
    private final MenuDtoAssembler menuDtoAssembler;

    @GetMapping("/menus")
    @PreAuthorize("isAuthenticated()")
    public Result<List<MenuVO>> getMyMenu() {
        return Result.success(menuDtoAssembler.toVOList(
                userPermissionService.getUserMenuTree(currentUserProvider.getUserId())));
    }

    @GetMapping("/codes")
    @PreAuthorize("isAuthenticated()")
    public Result<List<String>> getMyPermissionCodes() {
        return Result.success(
                userPermissionService.getUserPermissionCodes(currentUserProvider.getUserId()));
    }

}
