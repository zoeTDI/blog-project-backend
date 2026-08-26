package cn.caldm.www.auth_context.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
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
public class LoginUPCommand {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
