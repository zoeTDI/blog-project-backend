package cn.caldm.www.permission_context.adpater;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.permission_context.domain.repository.UserRoleRelationRepository;
import cn.caldm.www.user_context.application.service.UserRoleFacadeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleFacadeServiceImpl implements UserRoleFacadeService {

    private final UserRoleRelationRepository userRoleRelationRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<String> getUserRolesById(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<Role> roles = userRoleRelationRepository.getRolesByUserId(userId);
        return roles.stream().map(role -> role.getCode()).toList();
    }

    @Override
    public void assignRoleToUser(Long userId, String roleCode) {
        Role role = roleRepository.getRoleByCode(roleCode);
        userRoleRelationRepository.assignRoleToUser(userId, role.getId());
    }

    @Override
    public void assignRolesToUser(Long userId, List<String> roleCodes) {
        Assert.isNull(roleCodes, "The list of role code must not be null");
        Assert.notEmpty(roleCodes, "The list of role code must contain element");
        List<Role> roles = userRoleRelationRepository.getRolesByCodes(roleCodes);
        List<Long> roleIds = roles.stream().map(role -> role.getId()).filter(Objects::nonNull).toList();
        userRoleRelationRepository.assignRolesToUser(userId, roleIds);

    }

    @Override
    public void removeRoleFromUser(Long userId, String roleCode) {
        Role role = roleRepository.getRoleByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("The role is null.(roleCode: " + roleCode + ")");
        }
        userRoleRelationRepository.removeRoleFromUser(userId, role.getId());

    }

    @Override
    public void removeRolesFromUser(Long userId, List<String> roleCodes) {
        Assert.isNull(roleCodes, "The list of role code must be not null.");
        Assert.notEmpty(roleCodes, "The list of role code must contain element.");
        List<Role> roles = roleRepository.getRolesByCodes(roleCodes);
        List<Long> ids = roles.stream().map(role -> role.getId()).toList();
        userRoleRelationRepository.removeRolesFromUser(userId, ids);
    }

}
