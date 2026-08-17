package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.post_context.application.service.BlogPostService;
import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
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
public class BlogPostController {
    @Autowired
    private BlogPostService blogPostService;

    @PostMapping("/blogPost/add")
    public Result<Long> add(@RequestBody BlogPostCreateCommand command) {
        Long postId = blogPostService.createPost(command);
        if (postId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(postId);
    }

    @PostMapping("/blogPost/update")
    public Result update() {
        return Result.success();
    }

    @PostMapping("/blogPost/softDelete")
    public Result softDelete() {
        return Result.success();
    }

    @PostMapping("/blogPost/delete")
    public Result delete() {
        return Result.success();
    }
}
