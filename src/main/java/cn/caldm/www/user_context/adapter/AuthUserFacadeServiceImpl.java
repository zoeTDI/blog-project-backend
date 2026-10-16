package cn.caldm.www.user_context.adapter;

import cn.caldm.www.auth_context.application.service.AuthUserFacadeService;
import cn.caldm.www.auth_context.domain.model.AuthUser;
import cn.caldm.www.auth_context.domain.model.Permission;
import cn.caldm.www.user_context.application.service.UserRoleFacadeService;
import cn.caldm.www.user_context.domain.modal.SysUser;
import cn.caldm.www.user_context.domain.modal.UserRole;
import cn.caldm.www.user_context.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor 
public class AuthUserFacadeServiceImpl implements AuthUserFacadeService {

    private final UserRepository userRepository;
    private final UserRoleFacadeService userRoleFacadeService;

    @Override
    public AuthUser getCredentialByEmail(String email) {
        SysUser user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        Long id = user.getId();
        List<UserRole> roles = userRoleFacadeService.getUserRolesById(id);
        List<Long> roleIds = roles.stream().map(UserRole::getId).toList();
        List<Permission> permissions = userRoleFacadeService.getPermissionByRoleIds(roleIds);
        return toDto(user, roles, permissions);
    }

    @Override
    public AuthUser getCredentialByUsername(String username) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        Long id = user.getId();
        List<UserRole> roles = userRoleFacadeService.getUserRolesById(id);
        List<Long> roleIds = roles.stream().map(UserRole::getId).toList();
        List<Permission> permissions = userRoleFacadeService.getPermissionByRoleIds(roleIds);
        return toDto(user, roles, permissions);
    }

    @Override
    public AuthUser getCredentialById(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            return null;
        }
        List<UserRole> roles = userRoleFacadeService.getUserRolesById(id);
        List<Long> roleIds = roles.stream().map(UserRole::getId).toList();
        List<Permission> permissions = userRoleFacadeService.getPermissionByRoleIds(roleIds);
        return toDto(user, roles, permissions);
    }

    private AuthUser toDto(SysUser user, List<UserRole> roles, List<Permission> permissions) {
        AuthUser dto = new AuthUser();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setRoles(roles);
        dto.setPermissions(permissions);
        dto.setStatus(user.getStatus());
        dto.setDeleted(user.getDeleted());
        return dto;
    }
}
