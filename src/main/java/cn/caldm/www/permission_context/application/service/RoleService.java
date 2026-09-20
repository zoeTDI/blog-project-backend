package cn.caldm.www.permission_context.application.service;

import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.domain.repository.RoleRepository;
import cn.caldm.www.shared_kernel.domain.ResourceNotFoundException;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    Role getRoleById(@NonNull Long id) {
        Role role = roleRepository.getRoleById(id);
        return role.isEnabled() ? role : null;
    }

    Role getRoleByCode(@NonNull String code) {
        Role role = roleRepository.getRoleByCode(code);
        return role.isEnabled() ? role : null;
    }

    Map<String, Role> getRoleMap() {
        Map<String, Role> map = roleRepository.getRoleMap();
        map.entrySet().removeIf(entry -> !entry.getValue().isEnabled());
        return map;
    }

    void saveRole(@NonNull String name, @NonNull String code, @NonNull String sort, String remark) {
        Role newRole = new Role();
        newRole.setName(name);
        newRole.setCode(code);
        newRole.setSort(sort);
        newRole.setRemark(remark);
        roleRepository.saveRole(newRole);
    }

    void updateName(@NonNull Long targetId, @NonNull String name) {
        Role targetRole = isExist(targetId);
        targetRole.setName(name);
        roleRepository.updateRole(targetRole);
    }

    void updateCode(@NonNull Long targetId, @NonNull String code) {
        Role targetRole = isExist(targetId);
        targetRole.setCode(code);
        roleRepository.updateRole(targetRole);
    }

    void updateSort(@NonNull Long targetId, @NonNull String sort) {
        Role targetRole = isExist(targetId);
        targetRole.setCode(sort);
        roleRepository.updateRole(targetRole);
    }

    void updateRemark(@NonNull Long targetId, String remark) {
        Role targetRole = isExist(targetId);
        targetRole.setCode(remark);
        roleRepository.updateRole(targetRole);
    }

    void deleteRole(@NonNull Long targetId) {
        isExist(targetId);
        roleRepository.deleteRole(targetId);
    }

    private Role isExist(@NonNull Long targetId) {
        Role targetRole = roleRepository.getRoleById(targetId);
        if (targetRole == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND);
        }
        return targetRole;
    }
}
