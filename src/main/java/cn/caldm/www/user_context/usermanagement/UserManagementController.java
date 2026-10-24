package cn.caldm.www.user_context.usermanagement;

import cn.caldm.www.infrastructure.annotation.ApiAccessLog;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.user_context.domain.model.SysUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @ApiAccessLog(operateModule = "用户管理", operateName = "创建用户", operateType = 2)
    @PostMapping("/create")
    @PreAuthorize("@ss.isAdmin()")
    public Result<SysUser> create(@Valid @RequestBody CreateReqDTO createReqDTO) {
        return Result.success(userManagementService.create(
                createReqDTO.getUsername(),
                createReqDTO.getPassword(),
                createReqDTO.getEmail(),
                createReqDTO.getNickname(),
                createReqDTO.getRoleIds()));
    }

    @PostMapping("/ban")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> ban(@Valid @RequestBody BanReqDTO banReqDTO) {
        userManagementService.ban(banReqDTO.getTargetUserId());
        return Result.success();
    }

    @PostMapping("/softDelete")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> softDelete(@Valid @RequestBody SoftDeleteReqDTO softDeleteReqDTO) {
        userManagementService.softDelete(softDeleteReqDTO.getTargetUserId());
        return Result.success();
    }
}
