package cn.caldm.www.user_context.interfaces.dto;

import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class UserCredentialDTO {
    private Long id;
    private String username;
    private String password;
    private String salt;
    private SysUserStatusEnum status;
    private SysUserDeletedEnum deleted;
}
