package cn.caldm.www.post_context.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
public class BlogPostCategoryRelation {

    private Long id;
    private Long postId;
    private Long categoryId;
    private Boolean isDirect;

    /**
     * 判断是否为直接关联
     */
    public boolean isDirectRelation() {
        return Boolean.TRUE.equals(isDirect);
    }

    /**
     * 业务方法：标记为直接关联
     */
    public void markAsDirect() {
        this.isDirect = true;
    }

    /**
     * 业务方法：标记为祖先冗余
     */
    public void markAsIndirect() {
        this.isDirect = false;
    }
}
