package cn.caldm.www.auth_context.interfaces.dto;

import cn.caldm.www.user_context.domain.modal.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * 登录请求返回的信息
 *
 * @author caldm
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResDTO {
    private long id;
    private String email;
    private String username;
    private String nickname;
    private String avatar;
    private List<RoleEnum> roles;
    private List<String> menus;
}
