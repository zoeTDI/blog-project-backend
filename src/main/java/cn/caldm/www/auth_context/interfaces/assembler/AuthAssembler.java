package cn.caldm.www.auth_context.interfaces.assembler;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.user_context.interfaces.dto.UserCredentialDTO;

/**
 *
 *
 *
 * @author caldm
 */
public class AuthAssembler {

    public static AuthUser toAuthUser(UserCredentialDTO dto) {
        if (dto == null) {
            return null;
        }
        AuthUser user = new AuthUser();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());user.setPassword(dto.getPassword());
        user.setStatus(dto.getStatus());
        user.setDeleted(dto.getDeleted());
        return user;
    }
}
