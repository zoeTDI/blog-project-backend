package cn.caldm.www.user_context.interfaces.dto;

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
    private short status;
    private Boolean deleted;
}
