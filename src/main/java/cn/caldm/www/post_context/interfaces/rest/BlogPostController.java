package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.common.domain.PageResult;
import cn.caldm.www.post_context.application.service.BlogPostService;
import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
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

    @GetMapping("/blogPost/mine")
    public Result<PageResult<BlogPostSummaryDTO>> getCurrentUserPosts(@Valid @ModelAttribute BlogPostPageQueryDTO query) {
        PageResult<BlogPost> posts = blogPostService.getCurrentUserPosts(query.getPage(), query.getSize());
        return Result.success(posts.map(summaryAssembler::toDTO));
    }

    @GetMapping("/blogPost/{id}")
    public Result<BlogPostEditDTO> getBlogPost(@PathVariable("id") Long id) {
        return Result.success(editAssembler.toPO(blogPostService.getBlogPostById(id)));
    }

    @PostMapping("/blogPost/add")
    public Result<Long> add(@Valid @RequestBody BlogPostCreateCommand command) {
        Long postId = blogPostService.createPost(command);
        if (postId == null) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        return Result.success(postId);
    }

    @PostMapping("/blogPost/update")
    public Result<Void> update() {
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
