package cn.caldm.www.user_context.interfaces.internal;

import cn.caldm.www.user_context.interfaces.dto.UserCredentialDTO;

/**
 *
 *
 *
 * @author caldm
 */
public interface UserInternalService {

    UserCredentialDTO getCredentialByUsername(String username);

    UserCredentialDTO getCredentialById(Long id);
}
