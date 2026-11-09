package cn.caldm.www.permission_context.interfaces.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.caldm.www.permission_context.domain.model.Role;
import cn.caldm.www.permission_context.interfaces.dto.role.RoleResponse;

/**
 * 角色响应 DTO 装配回归钉：字段映射、空输入防御。
 */
public class RoleDtoAssemblerTest {

    private final RoleDtoAssembler assembler = new RoleDtoAssembler();

    @Test
    void toResponse_shouldReturnNullForNullInput() {
        assertThat(assembler.toResponse(null)).isNull();
    }

    @Test
    void toResponse_shouldMapAllFields() {
        Role role = Role.reconstitute(1L, "管理员", "ADMIN", 1, "系统管理员", true);

        RoleResponse response = assembler.toResponse(role);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("管理员");
        assertThat(response.getCode()).isEqualTo("ADMIN");
        assertThat(response.getSort()).isEqualTo(1);
        assertThat(response.getRemark()).isEqualTo("系统管理员");
        assertThat(response.isEnabled()).isTrue();
    }

    @Test
    void toResponse_shouldMapDisabledRole() {
        Role role = Role.reconstitute(2L, "停用角色", "DISABLED", 9, "", false);

        RoleResponse response = assembler.toResponse(role);

        assertThat(response.isEnabled()).isFalse();
    }

    @Test
    void toResponseList_shouldReturnEmptyForNullOrEmptyInput() {
        assertThat(assembler.toResponseList(null)).isEmpty();
        assertThat(assembler.toResponseList(List.of())).isEmpty();
    }

    @Test
    void toResponseList_shouldMapAllRoles() {
        Role admin = Role.reconstitute(1L, "管理员", "ADMIN", 1, "", true);
        Role author = Role.reconstitute(2L, "作者", "AUTHOR", 2, "", true);

        List<RoleResponse> responses = assembler.toResponseList(List.of(admin, author));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCode()).isEqualTo("ADMIN");
        assertThat(responses.get(1).getCode()).isEqualTo("AUTHOR");
    }
}
