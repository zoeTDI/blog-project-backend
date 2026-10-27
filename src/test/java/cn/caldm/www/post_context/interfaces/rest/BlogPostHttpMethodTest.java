package cn.caldm.www.post_context.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * HTTP 方法语义回归钉：状态变更与资源创建必须为 POST，
 * 防止未来改回 GET 引入副作用端点（P12/P13）。
 */
public class BlogPostHttpMethodTest {

    @Test
    void statusEndpoints_shouldBePost() throws NoSuchMethodException {
        Method review = BlogPostStatusController.class.getMethod("changeStatus", Long.class);
        assertThat(review.getAnnotation(PostMapping.class)).isNotNull();
        assertThat(review.getAnnotation(PostMapping.class).value()).containsExactly("/status/review");

        Method publish = BlogPostStatusController.class.getMethod("toPublished", Long.class);
        assertThat(publish.getAnnotation(PostMapping.class)).isNotNull();
        assertThat(publish.getAnnotation(PostMapping.class).value()).containsExactly("/status/published");
    }

    @Test
    void createTag_shouldBePost() throws NoSuchMethodException {
        Method createTag = BlogPostTagController.class.getMethod("createTag", String.class);
        assertThat(createTag.getAnnotation(PostMapping.class)).isNotNull();
        assertThat(createTag.getAnnotation(PostMapping.class).value()).containsExactly("/createTag");
    }
}
