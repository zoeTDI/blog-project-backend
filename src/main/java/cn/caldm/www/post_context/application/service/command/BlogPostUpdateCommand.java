package cn.caldm.www.post_context.application.service.command;

import java.util.List;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogPostUpdateCommand {
    @NotNull(message = "")
    private Long targetPostId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度超过限制")
    private String title;


    @Size(max = 200, message = "")
    private String subtitle;

    private String contentMd;
    private String contentHtml;
    @Size(max = 500, message = "")
    private String summary;

    private BlogPostTypeEnum type;

    private Boolean isTop;
    private Boolean isOriginal;
    private String slug;
    private String seoKeywords;
    private String seoDescription;

    @NotNull
    private Boolean allowComment;
    private String reprintSource;
    private Integer sortWeight;

    private List<Long> tagIds;
    private List<List<Long>> categoryIds;

    /**
     * 转换为领域对象（仅可编辑字段；未携带的字段保持 null，由仓库跳过）。
     */
    public BlogPost toDomain(Long id) {
        BlogPost post = new BlogPost();
        post.setId(id);
        post.setTitle(this.title);
        post.setSubtitle(this.subtitle);
        post.setContentMd(this.contentMd);
        post.setContentHtml(this.contentHtml);
        post.setSummary(this.summary);
        post.setType(this.type);
        post.setIsTop(this.isTop);
        post.setIsOriginal(this.isOriginal);
        post.setSlug(this.slug);
        post.setSeoKeywords(this.seoKeywords);
        post.setSeoDescription(this.seoDescription);
        post.setAllowComment(this.allowComment);
        post.setReprintSource(this.reprintSource);
        post.setSortWeight(this.sortWeight);
        return post;
    }

}
