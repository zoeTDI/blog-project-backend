package cn.caldm.www.post_context.application.service.command;

import cn.caldm.www.post_context.domain.model.BlogPostCategoryStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BlogPostCategoryCreateCommand {
    private Long parentId = 0L;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称不能超过 64 个字符")
    private String name;

    @Size(max = 128, message = "URL 别名不能超过 128 个字符")
    private String slug;

    @Size(max = 255, message = "分类描述不能超过 255 个字符")
    private String description;

    private Integer sortWeight = 0;

    private BlogPostCategoryStatusEnum status = BlogPostCategoryStatusEnum.ENABLED;
}
