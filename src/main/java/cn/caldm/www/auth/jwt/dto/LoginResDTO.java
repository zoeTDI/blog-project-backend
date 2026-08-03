package cn.caldm.www.auth.jwt.dto;

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
    private String role;
    private List<String> menus;
}
