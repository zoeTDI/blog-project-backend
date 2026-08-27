package cn.caldm.www.post_context.interfaces.dto;

import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Article projection used by list endpoints. Content and access password are intentionally omitted.
 */
@Data
public class BlogPostSummaryDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String summary;
    private BlogPostTypeEnum type;
    private BlogPostStatusEnum status;
    private Boolean isTop;
    private Boolean isOriginal;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishedTime;
    private Integer views;
    private Integer likes;
    private Integer collects;
    private Integer commentCount;
    private String slug;
    private Boolean allowComment;
    private Integer sortWeight;
}
