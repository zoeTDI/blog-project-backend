package cn.caldm.www.user_context.passwordreset;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class ResetPasswordReqDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String newPassword;
}
