package cn.caldm.www.auth_context.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 *
 * @author caldm
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginEPReqDTO {
    private String email;
    private String password;
}
