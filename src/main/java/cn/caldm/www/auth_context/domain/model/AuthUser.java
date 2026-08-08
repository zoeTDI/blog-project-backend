package cn.caldm.www.auth_context.domain.model;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import cn.caldm.www.user_context.domain.modal.SysUserDeletedEnum;
import cn.caldm.www.user_context.domain.modal.SysUserStatusEnum;
import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class AuthUser {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private List<RoleEnum> roles;
    private List<String> menus;
    private SysUserStatusEnum status;
    private SysUserDeletedEnum deleted;
    private String accessToken;
    private String refreshToken;
}
