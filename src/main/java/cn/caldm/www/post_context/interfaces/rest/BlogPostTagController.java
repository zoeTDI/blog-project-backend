package cn.caldm.www.post_context.interfaces.rest;

import cn.caldm.www.common.domain.Result;
import cn.caldm.www.common.domain.ResultCodeEnum;
import cn.caldm.www.post_context.application.service.BlogPostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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
    public Result createTag(@RequestParam("tagName") String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return Result.error(ResultCodeEnum.BAD_REQUEST);
        }
        Long tagId = tagService.createTag(tagName);
        if (tagId == null) {
            return Result.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return Result.success(Collections.singletonMap("tagId", tagId));
    }
}
