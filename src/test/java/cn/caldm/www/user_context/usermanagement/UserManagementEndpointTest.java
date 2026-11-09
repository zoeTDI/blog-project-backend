package cn.caldm.www.user_context.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户分页端点形状回归钉：
 * GET /user/page，仅管理员可达。
 */
public class UserManagementEndpointTest {

    @Test
    void getUserPage_shouldBeGetAndAdminOnly() throws NoSuchMethodException {
        Method getUserPage = UserManagementController.class.getMethod("getUserPage", Integer.class, Integer.class);
        assertThat(getUserPage.getAnnotation(GetMapping.class).value()).containsExactly("/page");
        assertThat(getUserPage.getAnnotation(PreAuthorize.class).value()).isEqualTo("@ss.isAdmin()");
    }
}
