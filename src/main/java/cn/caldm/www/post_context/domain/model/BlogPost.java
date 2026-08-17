package cn.caldm.www.post_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogPost implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long authorId;
    private String creator;
    private String updater;
    private String title;
    private String subtitle;
    private String contentMd;
    private String contentHtml;
    private String summary;
    private String tags;
    private BlogTypeEnum type;
    private BlogStatusEnum status;
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
    private String seoKeywords;
    private String seoDescription;
    private String password;
    private Boolean allowComment;
    private String reprintSource;
    private Integer sortWeight;
    private Boolean deleted;

    /**
     * 发布文章（从草稿转为已发布）
     */
    public void publish() {
        if (this.status == BlogStatusEnum.DRAFT) {
            this.status = BlogStatusEnum.PUBLISHED;
            this.publishedTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Only draft posts can be published");
        }
    }

    /**
     * 审核通过
     */
    public void approve() {
        if (this.status == BlogStatusEnum.REVIEWING) {
            this.status = BlogStatusEnum.PUBLISHED;
            this.publishedTime = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Only reviewing posts can be approved");
        }
    }

    /**
     * 归档（移入回收站）
     */
    public void archive() {
        if (this.status == BlogStatusEnum.PUBLISHED || this.status == BlogStatusEnum.DRAFT) {
            this.status = BlogStatusEnum.RECYCLE;
        } else {
            throw new IllegalStateException("Only published or draft posts can be archived");
        }
    }

    /**
     * 增加浏览量
     */
    public void incrementViews() {
        this.views = (this.views == null ? 0 : this.views) + 1;
    }

    /**
     * 增加点赞
     */
    public void incrementLikes() {
        this.likes = (this.likes == null ? 0 : this.likes) + 1;
    }

    /**
     * 增加收藏
     */
    public void incrementCollects() {
        this.collects = (this.collects == null ? 0 : this.collects) + 1;
    }

    /**
     * 是否置顶
     */
    public boolean isTop() {
        return Boolean.TRUE.equals(isTop);
    }

    /**
     * 是否原创
     */
    public boolean isOriginal() {
        return Boolean.TRUE.equals(isOriginal);
    }

    /**
     * 是否允许评论
     */
    public boolean allowComment() {
        return Boolean.TRUE.equals(allowComment);
    }

    /**
     * 是否需要密码访问
     */
    public boolean isPasswordProtected() {
        return password != null && !password.isEmpty();
    }
}
