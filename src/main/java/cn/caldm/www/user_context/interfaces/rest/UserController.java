package cn.caldm.www.user_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.infra.annotation.ApiAccessLog;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.user_context.application.service.UserApplicationService;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.interfaces.dto.BanReqDTO;
import cn.caldm.www.user_context.interfaces.dto.CreateReqDTO;
import cn.caldm.www.user_context.interfaces.dto.ResetPasswordReqDTO;
import cn.caldm.www.user_context.interfaces.dto.SoftDeleteReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserApplicationService userService;

    @ApiAccessLog(operateModule = "用户管理", operateName = "创建用户", operateType = 2)
    @PostMapping("/create")
    public Result<SysUser> create(@RequestBody CreateReqDTO createReqDTO) {
        Long creatorId = createReqDTO.getCreatorId();
        List<String> roleCodes = createReqDTO.getRoles();
        String username = createReqDTO.getUsername();
        String password = createReqDTO.getPassword();

        if (
                creatorId == null
                || roleCodes == null
                || roleCodes.isEmpty()
                || username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()
        ) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        List<RoleEnum> roles = roleCodes.stream()
                .map(RoleEnum::fromCode)
                .collect(Collectors.toList());
        SysUser newUser = userService.create(creatorId, username, password, roles);
        if (newUser == null) {
            return Result.error(ResultCodeEnum.FORBIDDEN);
        } else {
            return Result.success(newUser);
        }
    }

    @PostMapping("/ban")
    public Result ban(@RequestBody BanReqDTO banReqDTO) {
        Long updaterId = banReqDTO.getUpdaterId();
        Long targetUserId = banReqDTO.getTargetUserId();
        if (updaterId == null || targetUserId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        if (userService.ban(updaterId, targetUserId)) {
            return Result.success();
        } else {
            return Result.error(ResultCodeEnum.FORBIDDEN);
        }

    }

    @PostMapping("/softDelete")
    public Result softDelete(@RequestBody SoftDeleteReqDTO softDeleteReqDTO) {
        Long updaterId = softDeleteReqDTO.getUpdaterId();
        Long targetUserId = softDeleteReqDTO.getTargetUserId();
        if (updaterId == null || targetUserId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        if (userService.softDelete(updaterId, targetUserId)) {
            return Result.success();
        } else {
            return Result.error(ResultCodeEnum.FORBIDDEN);
        }

    }

    @PostMapping("/sendResetPasswordEmail")
    public Result sendResetPasswordEmail() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        boolean isSuccess = userService.sendPasswordResetEmail(userId);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody ResetPasswordReqDTO resetPasswordReqDTO) {
        Long id = SecurityContextHolder.getUserId();
        String code = resetPasswordReqDTO.getCode();
        String newPassword = resetPasswordReqDTO.getNewPassword();
        if (id == 0L || code == null || code.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        if (userService.resetPassword(id, code, newPassword)) {
            return Result.success();
        } else {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
