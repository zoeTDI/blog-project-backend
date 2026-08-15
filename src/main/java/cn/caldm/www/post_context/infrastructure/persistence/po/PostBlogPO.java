package cn.caldm.www.post_context.infrastructure.persistence.po;

import cn.caldm.www.post_context.domain.model.BlogStatusEnum;
import cn.caldm.www.post_context.domain.model.BlogTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 *
 *
 * @author caldm
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("post_blog")
public class PostBlogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 创建者
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    /**
     * 更新者
     */
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 副标题
     */
    @TableField("subtitle")
    private String subtitle;

    /**
     * 文章内容（markdown）
     */
    @TableField("content_md")
    private String contentMd;

    /**
     * 文章内容（HTML）
     */
    @TableField("content_html")
    private String contentHtml;

    /**
     * 文章摘要/简介
     */
    @TableField("summary")
    private String summary;

    /**
     * 文章标签（JSON 数组字符串，建议使用 JacksonTypeHandler 解析）
     */
    @TableField("tags")
    private String tags;

    /**
     * 文章类型（枚举）
     */
    @TableField("type")
    private BlogTypeEnum type;

    /**
     * 状态（枚举）
     */
    @TableField("status")
    private BlogStatusEnum status;

    /**
     * 是否置顶：true-置顶 false-不置顶
     */
    @TableField("is_top")
    private Boolean isTop;

    /**
     * 是否原创：true-原创 false-转载
     */
    @TableField("is_original")
    private Boolean isOriginal;

    /**
     * 创建时间（自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 实际发布时间（支持定时发布，若为NULL则默认等于create_time）
     */
    @TableField("published_time")
    private LocalDateTime publishedTime;

    /**
     * 阅读/浏览次数
     */
    @TableField("views")
    private Integer views;

    /**
     * 点赞数量
     */
    @TableField("likes")
    private Integer likes;

    /**
     * 收藏数量
     */
    @TableField("collects")
    private Integer collects;

    /**
     * 评论数量（冗余）
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * URL友好别名（slug，唯一索引）
     */
    @TableField("slug")
    private String slug;

    /**
     * SEO关键词
     */
    @TableField("seo_keywords")
    private String seoKeywords;

    /**
     * SEO页面描述
     */
    @TableField("seo_description")
    private String seoDescription;

    /**
     * 文章阅读密码（非空时表示需要密码访问）
     */
    @TableField("password")
    private String password;

    /**
     * 是否允许评论：true-允许 false-禁止
     */
    @TableField("allow_comment")
    private Boolean allowComment;

    /**
     * 转载来源（若转载，填写原文链接或出处）
     */
    @TableField("reprint_source")
    private String reprintSource;

    /**
     * 自定义排序权重（数值越大越靠前）
     */
    @TableField("sort_weight")
    private Integer sortWeight;

    /**
     * 软删除标识：true-已删除 false-未删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
