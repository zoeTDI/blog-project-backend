package cn.caldm.www.post_context.blogpost;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.shared_kernel.domain.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class BlogPostController {

    private final BlogPostService blogPostService;
    private final BlogPostSummaryAssembler summaryAssembler;
    private final BlogPostEditAssembler editAssembler;

    /**
     * 后台文章管理页面分页查询，需要作者id
     */
    @GetMapping("/blogPost/mine")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<PageResult<BlogPostSummaryDTO>> getCurrentUserPosts(
            @Valid @ModelAttribute BlogPostPageQueryDTO query) {
        PageResult<BlogPost> posts = blogPostService.getCurrentUserPosts(query.getPage(), query.getSize());
        return Result.success(posts.map(summaryAssembler::toDTO));
    }

    /**
     * 文章编辑
     */
    @GetMapping("/blogPost/edit")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<BlogPostEditDTO> getBlogPost(@RequestParam("id") Long id) {
        BlogPost domain = blogPostService.getBlogPostById(id);
        return Result.success(editAssembler.toDTO(domain));
    }

    @PostMapping("/blogPost/create")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Long> create(@Valid @RequestBody BlogPostCreateCommand command) {
        return Result.success(blogPostService.createPost(command));
    }

    @PostMapping("/blogPost/update")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> update(@Valid @RequestBody BlogPostUpdateCommand command) {
        blogPostService.updateBlogPost(command);
        return Result.success();
    }

    @PostMapping("/blogPost/softDelete")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> softDelete(@RequestParam("id") Long id) {
        blogPostService.softDeletePost(id);
        return Result.success();
    }
}
