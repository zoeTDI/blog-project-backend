package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.post_context.application.service.BlogPostTagService;
import cn.caldm.www.post_context.application.service.command.BlogPostTagRenameCommand;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 *
 *
 *
 * @author caldm
 */
@RestController
@RequestMapping("/post")
public class BlogPostTagController {
    @Autowired
    private BlogPostTagService tagService;

    @GetMapping("/createTag")
    public Result<Map<String, Object>> createTag(@RequestParam("tagName") String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        Long tagId = tagService.createTag(tagName);
        if (tagId == null) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return Result.success(Collections.singletonMap("tagId", tagId));
    }

    @PostMapping("/renameTag")
    public Result<Void> renameTag(@Valid @RequestBody BlogPostTagRenameCommand command) {
        tagService.renameTag(command);
        return Result.success();
    }

    @DeleteMapping("/deleteTag/{id}")
    public Result<Void> deleteTag(@Valid @PathVariable("id") Long targetTagId) {
        tagService.deleteTag(targetTagId);
        return Result.success();
    }
}
