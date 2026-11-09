package cn.caldm.www.permission_context.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 角色列表端点形状回归钉：
 * GET /permission/roles，仅管理员可达。
 */
public class RolePermissionEndpointTest {

    @Test
    void getAllRoles_shouldBeGetAndAdminOnly() throws NoSuchMethodException {
        Method getAllRoles = RolePermissionController.class.getMethod("getAllRoles");
        assertThat(getAllRoles.getAnnotation(GetMapping.class).value()).containsExactly("/roles");
        assertThat(getAllRoles.getAnnotation(PreAuthorize.class).value()).isEqualTo("@ss.isAdmin()");
    }
}
