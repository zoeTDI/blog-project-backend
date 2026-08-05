package cn.caldm.www.auth_context.domain.model;

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
public class AuthUser {
    private Long id;
    private String username;
    private String password;
    private SysUserStatusEnum status;
    private SysUserDeletedEnum deleted;
}
