package cn.caldm.www.post_context.interfaces.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.post_context.application.service.BlogPostService;

@RestController
@RequestMapping("/post")
public class BlogPostStatusController {

    @Autowired
    private BlogPostService postService;

    @GetMapping("/status/review")
    public Result<Void> changeStatus(@RequestParam(value = "postId", required = true) Long postId) {
        postService.toReview(postId);
        return Result.success();
    }

    @GetMapping("/status/published")
    public Result<Void> toPublished(@RequestParam(value = "postId", required = true) Long postId) {
        postService.toPublished(postId);
        return Result.success();
    }
}
