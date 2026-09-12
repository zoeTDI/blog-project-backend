package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.post_context.application.service.BlogPostCategoryService;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryCreateCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryRenameCommand;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.shared_kernel.security.SecurityUtils;
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

    @PostMapping("/createCategory")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Long> createCategory(@Valid @RequestBody BlogPostCategoryCreateCommand command) {
        Long categoryId = categoryService.createCategory(command);
        if (categoryId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(categoryId);
    }

    @DeleteMapping("/deleteCategory/{id}")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @PostMapping("/renameCategory")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> RenameCategory(@Valid @RequestBody BlogPostCategoryRenameCommand command) {
        if (command == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        categoryService.renameCategory(command);
        return Result.success();
    }

    @GetMapping("/getAllCategoriesByAuthor")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<List<CategoryTreeNode>> getAllCategoriesByAuthor() {
        Long userId = SecurityUtils.getUserId();
        List<CategoryTreeNode> selected = categoryService.getCategoryTreeByAuthorId(userId);
        return Result.success(selected);
    }

}
