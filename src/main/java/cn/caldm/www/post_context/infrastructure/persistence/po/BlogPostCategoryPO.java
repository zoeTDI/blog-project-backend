package cn.caldm.www.post_context.infrastructure.persistence.po;

import cn.caldm.www.post_context.domain.model.BlogPostCategoryStatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@TableName("blog_post_category")
public class BlogPostCategoryPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父级分类ID（0表示顶级）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * URL别名
     */
    @TableField("slug")
    private String slug;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;

    /**
     * 排序权重（数值越大越靠前）
     */
    @TableField("sort_weight")
    private Integer sortWeight;

    /**
     * 状态：0-禁用 1-启用
     */
    @TableField("status")
    private BlogPostCategoryStatusEnum status;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 软删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
