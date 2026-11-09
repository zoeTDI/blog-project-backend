package cn.caldm.www.permission_context.interfaces.assembler;

import java.util.List;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.interfaces.dto.role.RoleResponse;
import org.springframework.stereotype.Component;

/**
 * 角色 DTO 装配器。
 *
 * @author caldm
 */
@Component
public class RoleDtoAssembler {

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setCode(role.getCode());
        response.setSort(role.getSort());
        response.setRemark(role.getRemark());
        response.setEnabled(role.isEnabled());
        return response;
    }

    public List<RoleResponse> toResponseList(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(this::toResponse)
                .toList();
    }
}
