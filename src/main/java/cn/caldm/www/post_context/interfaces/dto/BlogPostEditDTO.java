package cn.caldm.www.post_context.interfaces.dto;

import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
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
public class BlogPostEditDTO {
    private Long id;
    private Long authorId;
    private String creator;
    private String updater;
    private String title;
    private String subtitle;
    private String contentMd;
    private String contentHtml;
    private String summary;
    private List<Long> tags;
    private List<List<Long>> categories;
    private BlogPostTypeEnum type;
    private BlogPostStatusEnum status;
    private Boolean isTop;
    private Boolean isOriginal;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishedTime;
    private String slug;
    private String seoKeywords;
    private String seoDescription;
    private String password;
    private Boolean allowComment;
    private String reprintSource;
    private Integer sortWeight;
}
