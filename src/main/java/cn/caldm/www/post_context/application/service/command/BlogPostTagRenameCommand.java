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
public class BlogPostTagRenameCommand {
    @NotNull(message = "目标tag的id不能为空")
    private Long targetTagId;
    @Size(max = 64, message = "tag名称不能超过64个字符")
    @NotBlank(message = "tag名称不能为空")
    private String newName;
}
