package cn.caldm.www.user_context.adapter;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class AuthUserFacadeServiceImpl implements AuthUserFacadeService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthUser getCredentialByEmail(String email) {
        SysUser sysUser = userRepository.findByEmail(email);
        if (sysUser == null) {
            return null;
        }
        return toDto(sysUser);
    }

    @Override
    public AuthUser getCredentialByUsername(String username) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        return toDto(user);
    }

    @Override
    public AuthUser getCredentialById(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            return null;
        }

        return toDto(user);
    }

    private AuthUser toDto(SysUser user) {
        AuthUser dto = new AuthUser();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setRoles(user.getRoles());
        dto.setMenus(user.getMenus());
        dto.setStatus(user.getStatus());
        dto.setDeleted(user.getDeleted());
        return dto;
    }
}
