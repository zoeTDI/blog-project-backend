package cn.caldm.www.user_context.interfaces.internal;

import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import cn.caldm.www.user_context.interfaces.dto.UserCredentialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class UserInternalServiceImpl implements UserInternalService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserCredentialDTO getCredentialByUsername(String username) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        return toDto(user);
    }

    @Override
    public UserCredentialDTO getCredentialById(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            return null;
        }

        return toDto(user);
    }

    private UserCredentialDTO toDto(SysUser user) {
        UserCredentialDTO dto = new UserCredentialDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setSalt(user.getSalt());
        dto.setStatus(user.getStatus());
        dto.setDeleted(user.getDeleted());
        return dto;
    }
}
