package cn.caldm.www.user_context.interfaces.rest;

import cn.caldm.www.infrastructure.annotation.ApiAccessLog;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.user_context.application.service.UserApplicationService;
import cn.caldm.www.user_context.domain.model.SysUser;
import cn.caldm.www.user_context.interfaces.dto.BanReqDTO;
import cn.caldm.www.user_context.interfaces.dto.CreateReqDTO;
import cn.caldm.www.user_context.interfaces.dto.ResetPasswordReqDTO;
import cn.caldm.www.user_context.interfaces.dto.SoftDeleteReqDTO;
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
public class UserController {

    private final UserApplicationService userService;
    private final CurrentUserProvider currentUserProvider;

    @ApiAccessLog(operateModule = "用户管理", operateName = "创建用户", operateType = 2)
    @PostMapping("/create")
    @PreAuthorize("@ss.isAdmin()")
    public Result<SysUser> create(@Valid @RequestBody CreateReqDTO createReqDTO) {
        return Result.success(userService.create(
                createReqDTO.getUsername(),
                createReqDTO.getPassword(),
                createReqDTO.getEmail(),
                createReqDTO.getNickname(),
                createReqDTO.getRoleIds()));
    }

    @PostMapping("/ban")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> ban(@Valid @RequestBody BanReqDTO banReqDTO) {
        userService.ban(banReqDTO.getTargetUserId());
        return Result.success();
    }

    @PostMapping("/softDelete")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> softDelete(@Valid @RequestBody SoftDeleteReqDTO softDeleteReqDTO) {
        userService.softDelete(softDeleteReqDTO.getTargetUserId());
        return Result.success();
    }

    @PostMapping("/sendResetPasswordEmail")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> sendResetPasswordEmail() {
        Long userId = currentUserProvider.getUserId();
        if (userId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        userService.sendPasswordResetEmail(userId);
        return Result.success();

    }

    @PostMapping("/resetPassword")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordReqDTO resetPasswordReqDTO) {
        userService.resetPassword(
                currentUserProvider.getUserId(),
                resetPasswordReqDTO.getCode(),
                resetPasswordReqDTO.getNewPassword());
        return Result.success();
    }
}
