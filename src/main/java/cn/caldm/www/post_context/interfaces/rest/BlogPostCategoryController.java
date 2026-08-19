package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.post_context.application.service.BlogPostCategoryService;
import cn.caldm.www.post_context.application.service.command.BlogPostCategoryCreateCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private BlogPostCategoryService categoryService;

    @PostMapping("/createCategory")
    public Result createCategory(@Valid @RequestBody BlogPostCategoryCreateCommand command) {
        Long categoryId = categoryService.createCategory(command);
        if (categoryId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(categoryId);
    }
}
