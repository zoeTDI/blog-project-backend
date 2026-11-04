package cn.caldm.www.post_context.blogcategory;

import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import cn.caldm.www.shared_kernel.security.PermissionCodes;
import cn.caldm.www.shared_kernel.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class BlogPostCategoryController {

    private final BlogPostCategoryService categoryService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/createCategory")
    @RequirePermission(PermissionCodes.CATEGORY_MANAGE)
    public Result<Long> createCategory(@Valid @RequestBody BlogPostCategoryCreateCommand command) {
        return Result.success(categoryService.createCategory(command));
    }

    @DeleteMapping("/deleteCategory/{id}")
    @RequirePermission(PermissionCodes.CATEGORY_MANAGE)
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @PostMapping("/renameCategory")
    @RequirePermission(PermissionCodes.CATEGORY_MANAGE)
    public Result<Void> renameCategory(@Valid @RequestBody BlogPostCategoryRenameCommand command) {
        categoryService.renameCategory(command);
        return Result.success();
    }

    @GetMapping("/getAllCategoriesByAuthor")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<List<CategoryTreeNode>> getAllCategoriesByAuthor() {
        Long userId = currentUserProvider.getUserId();
        List<CategoryTreeNode> selected = categoryService.getCategoryTreeByAuthorId(userId);
        return Result.success(selected);
    }

}
