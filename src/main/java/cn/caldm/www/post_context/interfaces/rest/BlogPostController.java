package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/blogPost/add")
    public Result add() {
        return Result.success();
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
