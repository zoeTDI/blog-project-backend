package cn.caldm.www.post_context.application.service.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BlogPostCategoryRenameCommand {
    @NotNull(message = "目标分类 id 不能为空")
    private Long targetId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称不能超过64个字符")
    private String newName;
}
