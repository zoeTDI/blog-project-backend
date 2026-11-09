package cn.caldm.www.post_context.publicpost;

import cn.caldm.www.post_context.blogpost.BlogPostService;
import cn.caldm.www.post_context.blogpost.BlogPostSummaryAssembler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.caldm.www.infrastructure.annotation.Anonymous;
import cn.caldm.www.post_context.blogpost.BlogPostSummaryDTO;
import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.shared_kernel.domain.Result;
import lombok.RequiredArgsConstructor;

@Anonymous
@RestController
@RequestMapping("/public/post")
@RequiredArgsConstructor
public class PublicPostController {

    private final BlogPostService blogPostService;
    private final BlogPostSummaryAssembler summaryAssembler;
    private final PublicPostAssembler publicPostAssembler;

    @GetMapping("/list")
    public Result<PageResult<BlogPostSummaryDTO>> getPublishedPostPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return Result.success(blogPostService.getPublishedPosts(page, size).map(summaryAssembler::toDTO));
    }

    @GetMapping("/detail")
    public Result<PublicPostDTO> getPublishedPostDetail(@RequestParam(value = "id") Long id) {
        return Result.success(
                publicPostAssembler.toDto(
                        blogPostService.getPublishedPostDetail(id)));
    }
}
