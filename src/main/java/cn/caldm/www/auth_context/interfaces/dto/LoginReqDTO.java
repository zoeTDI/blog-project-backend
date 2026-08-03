package cn.caldm.www.auth_context.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 请求时携带的信息
 *
 * @author caldm
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginReqDTO {
    private String username;
    private String password;
}
