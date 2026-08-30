package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.common.domain.PageResult;
import cn.caldm.www.post_context.application.service.BlogPostService;
import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostUpdateCommand;
import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostEditAssembler;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostSummaryAssembler;
import cn.caldm.www.post_context.interfaces.dto.BlogPostEditDTO;
import cn.caldm.www.post_context.interfaces.dto.BlogPostPageQueryDTO;
import cn.caldm.www.post_context.interfaces.dto.BlogPostSummaryDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private BlogPostSummaryAssembler summaryAssembler;
    @Autowired
    private BlogPostEditAssembler editAssembler;

    /**
     * 后台文章管理页面分页查询，需要作者id
     */
    @GetMapping("/blogPost/mine")
    public Result<PageResult<BlogPostSummaryDTO>> getCurrentUserPosts(
            @Valid @ModelAttribute BlogPostPageQueryDTO query) {
        PageResult<BlogPost> posts = blogPostService.getCurrentUserPosts(query.getPage(), query.getSize());
        return Result.success(posts.map(summaryAssembler::toDTO));
    }

    /**
     * 文章编辑
     */
    @GetMapping("/blogPost/edit")
    public Result<BlogPostEditDTO> getBlogPost(@RequestParam("id") Long id) {
        BlogPost domain = blogPostService.getBlogPostById(id);
        return Result.success(editAssembler.toPO(domain));
    }

    @PostMapping("/blogPost/create")
    public Result<Long> create(@Valid @RequestBody BlogPostCreateCommand command) {
        Long postId = blogPostService.createPost(command);
        if (postId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(postId);
    }

    @PostMapping("/blogPost/update")
    public Result<Void> update(@Valid @RequestBody BlogPostUpdateCommand command) {
        blogPostService.updateBlogPost(command);
        return Result.success();
    }

    @PostMapping("/blogPost/softDelete")
    public Result<Void> softDelete() {
        return Result.success();
    }

    @PostMapping("/blogPost/delete")
    public Result<Void> delete() {
        return Result.success();
    }
}
