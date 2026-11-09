package cn.caldm.www.post_context.publicpost;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import lombok.Data;

@Data 
public class PublicPostDTO {
    private Long id;
    private Long authorId;
    // creator
    private String author;
    private String title;
    private String subtitle;
    private String contentMd;
    private String contentHtml;
    private String summary;
    private BlogPostTypeEnum type;
    private Boolean isTop;
    private Boolean isOriginal;
    private LocalDateTime createTime;
    private LocalDateTime publishedTime;
    private Integer views;
    private Integer likes;
    private Integer collects;
    private Integer commentCount;
    private String slug;
    private String seoKeywords;
    private String seoDescription;
    private Boolean allowComment;
    private String reprintSource;
    private List<BlogPostTag> tags = new ArrayList<>();
    private List<CategoryTreeNode> categories = new ArrayList<>();
}
