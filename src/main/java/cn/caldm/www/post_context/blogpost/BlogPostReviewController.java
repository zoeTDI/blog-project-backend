package cn.caldm.www.post_context.blogpost;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.security.PermissionCodes;
import cn.caldm.www.shared_kernel.security.RequirePermission;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post/review")
@RequiredArgsConstructor
public class BlogPostReviewController {

    private final BlogPostService postService;
    private final BlogPostSummaryAssembler summaryAssembler;
    private final BlogPostEditAssembler editAssembler;

    @GetMapping("/list")
    @RequirePermission(PermissionCodes.POST_PUBLISH)
    public Result<PageResult<BlogPostSummaryDTO>> getReviewingPostPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageResult<BlogPost> reviewQueue = postService.getReviewQueue(page, size);
        return Result.success(reviewQueue.map(summaryAssembler::toDTO));
    }

    @GetMapping("/detail")
    @RequirePermission(PermissionCodes.POST_PUBLISH)
    public Result<BlogPostEditDTO> getReviewingDetail(@RequestParam(value = "id") Long id) {
        BlogPost detail = postService.getReviewDetail(id);
        return Result.success(editAssembler.toDTO(detail));

    }

}
