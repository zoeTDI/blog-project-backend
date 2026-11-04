package cn.caldm.www.post_context.blogpost;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import cn.caldm.www.post_context.blogcategory.BlogPostCategoryController;
import cn.caldm.www.post_context.blogcategory.BlogPostCategoryCreateCommand;
import cn.caldm.www.post_context.blogcategory.BlogPostCategoryRenameCommand;
import cn.caldm.www.post_context.blogtag.BlogPostTagController;
import cn.caldm.www.post_context.blogtag.BlogPostTagRenameCommand;
import cn.caldm.www.shared_kernel.security.PermissionCodes;
import cn.caldm.www.shared_kernel.security.RequirePermission;

/**
 * 端点 ↔ 权限码映射回归钉。
 * 防止未来把权限码改错（如 POST_DELETE 换成 POST_CREATE）或退回角色守卫而不自知。
 * 角色边界：作者=create/update/delete/submit/category/tag，审核员=publish，管理员=豁免。
 */
public class BlogPostAuthorizationTest {

    @Test
    void blogPostMutationEndpoints_shouldUseExpectedPermissionCodes() throws NoSuchMethodException {
        assertPermissionGuard(BlogPostController.class.getMethod("create", BlogPostCreateCommand.class),
                PermissionCodes.POST_CREATE);
        assertPermissionGuard(BlogPostController.class.getMethod("update", BlogPostUpdateCommand.class),
                PermissionCodes.POST_UPDATE);
        assertPermissionGuard(BlogPostController.class.getMethod("softDelete", Long.class),
                PermissionCodes.POST_DELETE);
    }

    @Test
    void blogPostStatusEndpoints_shouldUseExpectedPermissionCodes() throws NoSuchMethodException {
        // 提交审核：作者（草稿→审核中）；发布：审核员（审核中→已发布），二者边界不同不可混用
        assertPermissionGuard(BlogPostStatusController.class.getMethod("changeStatus", Long.class),
                PermissionCodes.POST_SUBMIT);
        assertPermissionGuard(BlogPostStatusController.class.getMethod("toPublished", Long.class),
                PermissionCodes.POST_PUBLISH);
    }

    @Test
    void categoryEndpoints_shouldUseCategoryManage() throws NoSuchMethodException {
        assertPermissionGuard(
                BlogPostCategoryController.class.getMethod("createCategory", BlogPostCategoryCreateCommand.class),
                PermissionCodes.CATEGORY_MANAGE);
        assertPermissionGuard(
                BlogPostCategoryController.class.getMethod("deleteCategory", Long.class),
                PermissionCodes.CATEGORY_MANAGE);
        assertPermissionGuard(
                BlogPostCategoryController.class.getMethod("renameCategory", BlogPostCategoryRenameCommand.class),
                PermissionCodes.CATEGORY_MANAGE);
    }

    @Test
    void tagEndpoints_shouldUseTagManage() throws NoSuchMethodException {
        assertPermissionGuard(
                BlogPostTagController.class.getMethod("createTag", String.class),
                PermissionCodes.TAG_MANAGE);
        assertPermissionGuard(
                BlogPostTagController.class.getMethod("renameTag", BlogPostTagRenameCommand.class),
                PermissionCodes.TAG_MANAGE);
        assertPermissionGuard(
                BlogPostTagController.class.getMethod("deleteTag", Long.class),
                PermissionCodes.TAG_MANAGE);
    }

    private void assertPermissionGuard(Method method, PermissionCodes expected) {
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        assertThat(annotation)
                .as("%s 缺少 @RequirePermission 权限守卫", method)
                .isNotNull();
        assertThat(annotation.value())
                .as("%s 的权限码与期望不符", method)
                .isEqualTo(expected);
    }
}
