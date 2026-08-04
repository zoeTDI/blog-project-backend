package cn.caldm.www.auth_context.infrastructure.repository;

import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.repository.UserRepository;
import cn.caldm.www.auth_context.interfaces.assembler.AuthAssembler;
import cn.caldm.www.user_context.interfaces.dto.UserCredentialDTO;
import cn.caldm.www.user_context.interfaces.internal.UserInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 *
 *
 * @author caldm
 */
@Repository("authContextUserRepositoryImpl")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserInternalService userInternalService;

    @Override
    public AuthUser findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        UserCredentialDTO dto = userInternalService.getCredentialByUsername(username);
        return AuthAssembler.toAuthUser(dto);
    }

    @Override
    public AuthUser findById(Long id) {
        if (id == null) {
            return null;
        }
        UserCredentialDTO dto = userInternalService.getCredentialById(id);
        return AuthAssembler.toAuthUser(dto);
    }

}
