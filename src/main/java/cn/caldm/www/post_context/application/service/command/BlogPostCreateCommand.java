package cn.caldm.www.post_context.application.service.command;

import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class BlogPostCreateCommand {
    @NotNull
    @NotBlank
    @Size(max = 200, message = "标题长度超过限制")
    private String title;
    @Size(max = 200, message = "副标题长度超过限制")
    private String subtitle;
    private String contentMd;
    private String contentHtml;
    @Size(max = 500, message = "摘要内容超过限制")
    private String summary;
    private BlogPostTypeEnum type;
    private BlogPostStatusEnum status;
    @NotNull(message = "置顶状态不允许为空")
    private Boolean isTop;
    @NotNull(message = "原创状态不允许为空")
    private Boolean isOriginal;
    private LocalDateTime publishedTime;
    private String slug;
    private String seoKeywords;
    private String seoDescription;
    private String password;
    @NotNull(message = "评论开关设置不允许为空")
    private Boolean allowComment;
    private String reprintSource;
    @NotNull(message = "排序权重不允许为空")
    private Integer sortWeight;

    private List<Long> tagIds;
    private List<CategoryNodeParam> categoryTrees;
}
