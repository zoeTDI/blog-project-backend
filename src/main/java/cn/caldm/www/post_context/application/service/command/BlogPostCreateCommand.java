package cn.caldm.www.post_context.application.service.command;

import cn.caldm.www.post_context.domain.model.BlogPost;
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
    @NotNull(message = "置顶状态不允许为空")
    private Boolean isTop;
    @NotNull(message = "原创状态不允许为空")
    private Boolean isOriginal;
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
    private List<List<Long>> categoryTrees;

    /**
     * 转换为领域对象：创建场景默认值在此统一（草稿状态、计数归零、审计时间）。
     */
    public BlogPost toDomain() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(this.title);
        blogPost.setSubtitle(this.subtitle);
        blogPost.setContentMd(this.contentMd);
        blogPost.setContentHtml(this.contentHtml);
        blogPost.setSummary(this.summary);
        blogPost.setType(this.type);
        blogPost.setStatus(BlogPostStatusEnum.DRAFT);
        blogPost.setIsTop(this.isTop);
        blogPost.setIsOriginal(this.isOriginal);
        blogPost.setPublishedTime(null);
        blogPost.setSlug(this.slug);
        blogPost.setSeoKeywords(this.seoKeywords);
        blogPost.setSeoDescription(this.seoDescription);
        blogPost.setPassword(this.password);
        blogPost.setAllowComment(this.allowComment);
        blogPost.setReprintSource(this.reprintSource);
        blogPost.setSortWeight(this.sortWeight);
        LocalDateTime now = LocalDateTime.now();
        blogPost.setCreateTime(now);
        blogPost.setUpdateTime(now);
        blogPost.setViews(0);
        blogPost.setLikes(0);
        blogPost.setCollects(0);
        blogPost.setCommentCount(0);
        return blogPost;
    }

}
