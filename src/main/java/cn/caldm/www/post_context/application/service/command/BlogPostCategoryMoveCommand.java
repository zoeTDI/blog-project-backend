package cn.caldm.www.post_context.application.service.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BlogPostCategoryMoveCommand {
    /**
     * 目标父级分类ID（0表示移动为顶级分类）
     */
    @NotNull(message = "目标父级分类ID不能为空")
    private Long targetParentId;
}
