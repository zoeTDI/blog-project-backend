package cn.caldm.www.auth_context.application.service;

import cn.caldm.www.auth_context.domain.model.AuthUser;

public interface AuthUserFacadeService {
    AuthUser getCredentialByEmail(String email);

    AuthUser getCredentialByUsername(String username);

    AuthUser getCredentialById(Long id);
}
