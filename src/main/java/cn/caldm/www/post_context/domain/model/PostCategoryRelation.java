package cn.caldm.www.post_context.domain.model;

import lombok.Data;

/**
 *
 *
 *
 * @author caldm
 */
@Data
public class PostCategoryRelation {
    private Long id;
    private Long postId;
    private Long categoryId;
    private Boolean isDirect;
    private Boolean deleted;

    /**
     * 判断是否为直接关联
     */
    public boolean isDirectRelation() {
        return Boolean.TRUE.equals(isDirect);
    }

    /**
     * 判断是否已删除
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
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
