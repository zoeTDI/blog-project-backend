package cn.caldm.www.post_context.blogpost;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import cn.caldm.www.post_context.blogtag.BlogPostTagController;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Test
    void reviewEndpoints_shouldBeGetAndRejectShouldBePost() throws NoSuchMethodException {
        Method list = BlogPostReviewController.class.getMethod("getReviewingPostPage", Integer.class, Integer.class);
        assertThat(list.getAnnotation(GetMapping.class).value()).containsExactly("/list");

        Method detail = BlogPostReviewController.class.getMethod("getReviewingDetail", Long.class);
        assertThat(detail.getAnnotation(GetMapping.class).value()).containsExactly("/detail");

        Method reject = BlogPostStatusController.class.getMethod("reject", Long.class);
        assertThat(reject.getAnnotation(PostMapping.class).value()).containsExactly("/status/reject");
    }
}
