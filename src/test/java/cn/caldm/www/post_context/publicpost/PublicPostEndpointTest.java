package cn.caldm.www.post_context.publicpost;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.caldm.www.infrastructure.annotation.Anonymous;
import cn.caldm.www.shared_kernel.security.RequirePermission;

/**
 * 游客通道端点形状回归钉：
 * 公开端点必须类级 @Anonymous 放行、GET 语义、且不挂任何权限/认证守卫。
 */
public class PublicPostEndpointTest {

    @Test
    void publicEndpoints_shouldBeAnonymousGetWithoutPermissionGuard() throws NoSuchMethodException {
        Class<?> controller = PublicPostController.class;

        // 路径前缀隔离在 /public/post，与管理端点 /post 分离
        RequestMapping mapping = controller.getAnnotation(RequestMapping.class);
        assertThat(mapping.value()).containsExactly("/public/post");

        // 类级放行游客
        assertThat(controller.getAnnotation(Anonymous.class)).isNotNull();

        Method list = controller.getMethod("getPublishedPostPage", Integer.class, Integer.class);
        assertThat(list.getAnnotation(GetMapping.class).value()).containsExactly("/list");
        assertNoGuard(list);

        Method detail = controller.getMethod("getPublishedPostDetail", Long.class);
        assertThat(detail.getAnnotation(GetMapping.class).value()).containsExactly("/detail");
        assertNoGuard(detail);
    }

    private void assertNoGuard(Method method) {
        assertThat(method.getAnnotation(RequirePermission.class))
                .as("%s 不应挂权限守卫（游客通道）", method)
                .isNull();
        assertThat(method.getAnnotation(PreAuthorize.class))
                .as("%s 不应挂认证守卫（游客通道）", method)
                .isNull();
    }
}
