package cn.caldm.www.post_context.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文章-分类关联表 PO
 * 对应表名：post_category_relation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("blog_post_category_relation")
public class BlogPostCategoryRelationPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 是否直接关联：true-用户直接选择的分类，false-系统自动插入的祖先冗余
     * 数据库默认值为 b'1'
     */
    @TableField("is_direct")
    private Boolean isDirect;

}
