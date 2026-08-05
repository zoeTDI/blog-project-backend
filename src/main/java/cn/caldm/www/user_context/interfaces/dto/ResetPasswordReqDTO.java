package cn.caldm.www.user_context.interfaces.dto;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class ResetPasswordReqDTO {
    private String code;
    private String newPassword;
}
