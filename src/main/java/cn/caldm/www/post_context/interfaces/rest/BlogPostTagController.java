package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.post_context.application.service.BlogPostTagService;
import cn.caldm.www.post_context.application.service.command.BlogPostTagRenameCommand;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.shared_kernel.domain.Result;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class BlogPostTagController {

    private final BlogPostTagService tagService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/createTag")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Map<String, Object>> createTag(@RequestParam("tagName") String tagName) {
        return Result.success(Collections.singletonMap("tagId", tagService.createTag(tagName)));
    }

    @PostMapping("/renameTag")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> renameTag(@Valid @RequestBody BlogPostTagRenameCommand command) {
        tagService.renameTag(command);
        return Result.success();
    }

    @DeleteMapping("/deleteTag/{id}")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<Void> deleteTag(@Valid @PathVariable("id") Long targetTagId) {
        tagService.deleteTag(targetTagId);
        return Result.success();
    }

    @GetMapping("/getAllTagsByAuthor")
    @PreAuthorize("@ss.isContentOperator()")
    public Result<List<BlogPostTag>> getAllTags() {
        Long authorId = currentUserProvider.getUserId();
        return Result.success(tagService.getTagsByAuthorId(authorId));
    }
}
