package cn.caldm.www.auth_context.domain.model;

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
    private String salt;
    private short status;
    private Boolean deleted;
}
