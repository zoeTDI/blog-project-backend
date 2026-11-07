package cn.caldm.www.permission_context.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 动态路由两个出口的端点形状回归钉：
 * GET /permission/me/menus 与 /permission/me/codes，均要求登录态。
 */
public class UserPermissionEndpointTest {

    @Test
    void menuEndpoints_shouldBeGetAndAuthenticated() throws NoSuchMethodException {
        Method menus = UserPermissionController.class.getMethod("getMyMenu");
        assertThat(menus.getAnnotation(GetMapping.class).value()).containsExactly("/menus");
        assertThat(menus.getAnnotation(PreAuthorize.class).value()).isEqualTo("isAuthenticated()");

        Method codes = UserPermissionController.class.getMethod("getMyPermissionCodes");
        assertThat(codes.getAnnotation(GetMapping.class).value()).containsExactly("/codes");
        assertThat(codes.getAnnotation(PreAuthorize.class).value()).isEqualTo("isAuthenticated()");
    }
}
