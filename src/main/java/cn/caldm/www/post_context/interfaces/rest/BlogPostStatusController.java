package cn.caldm.www.post_context.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cn.caldm.www.post_context.application.service.BlogPostService;
import cn.caldm.www.shared_kernel.domain.Result;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class BlogPostStatusController {

    private final BlogPostService postService;

    @PostMapping("/status/review")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> changeStatus(@RequestParam(value = "postId", required = true) Long postId) {
        postService.toReview(postId);
        return Result.success();
    }

    @PostMapping("/status/published")
    @PreAuthorize("@ss.isManager()")
    public Result<Void> toPublished(@RequestParam(value = "postId", required = true) Long postId) {
        postService.toPublished(postId);
        return Result.success();
    }
}
