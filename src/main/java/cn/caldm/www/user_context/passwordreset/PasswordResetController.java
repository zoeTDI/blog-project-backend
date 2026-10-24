package cn.caldm.www.user_context.passwordreset;

import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
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
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/sendResetPasswordEmail")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> sendResetPasswordEmail() {
        passwordResetService.sendPasswordResetEmail(currentUserProvider.getUserId());
        return Result.success();
    }

    @PostMapping("/resetPassword")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordReqDTO resetPasswordReqDTO) {
        passwordResetService.resetPassword(
                currentUserProvider.getUserId(),
                resetPasswordReqDTO.getCode(),
                resetPasswordReqDTO.getNewPassword());
        return Result.success();
    }
}
