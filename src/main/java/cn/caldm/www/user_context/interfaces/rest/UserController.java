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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Result<SysUser> create(@RequestBody CreateReqDTO createReqDTO) {
        List<Long> roleIds = createReqDTO.getRoleIds();
        String username = createReqDTO.getUsername();
        String password = createReqDTO.getPassword();
        String email = createReqDTO.getEmail();

        if (
                roleIds == null
                || roleIds.isEmpty()
                || username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()
                || email == null
                || email.isEmpty()
        ) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(userService.create(username, password, email, createReqDTO.getNickname(), roleIds));
    }

    @PostMapping("/ban")
    @PreAuthorize("@ss.isAdmin()")
    public Result<Void> ban(@RequestBody BanReqDTO banReqDTO) {
        Long targetUserId = banReqDTO.getTargetUserId();
        if (targetUserId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        userService.ban(targetUserId);
        return Result.success();

    }

    @PostMapping("/softDelete")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> softDelete(@RequestBody SoftDeleteReqDTO softDeleteReqDTO) {
        Long targetUserId = softDeleteReqDTO.getTargetUserId();
        if (targetUserId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        userService.softDelete(targetUserId);
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
    public Result<Void> resetPassword(@RequestBody ResetPasswordReqDTO resetPasswordReqDTO) {
        Long id = currentUserProvider.getUserId();
        String code = resetPasswordReqDTO.getCode();
        String newPassword = resetPasswordReqDTO.getNewPassword();
        if (id == 0L || code == null || code.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }

        userService.resetPassword(id, code, newPassword);
        return Result.success();
    }
}
