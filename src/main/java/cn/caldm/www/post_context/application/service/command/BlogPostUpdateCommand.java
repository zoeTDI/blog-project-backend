package cn.caldm.www.post_context.application.service.command;

import java.time.LocalDateTime;
import java.util.List;

import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogPostUpdateCommand {
    @NotNull(message = "")
    private Long targetPostId;

    @Size(max = 200, message = "")
    private String title;

    @Size(max = 200, message = "")
    private String subtitle;

    private String contentMd;
    private String contentHtml;
    @Size(max = 500, message = "")
    private String summary;

    private BlogPostTypeEnum type;
    private BlogPostStatusEnum status;

    private Boolean isTop;
    private Boolean isOriginal;
    private LocalDateTime publishedTime;
    private String slug;
    private String seoKeywords;
    private String seoDescription;

    @NotNull
    private Boolean allowComment;
    private String reprintSource;
    private Integer sortWeight;

    private List<Long> tagIds;
    private List<List<Long>> categoryIds;
}
