package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;

import java.util.List;

public interface BlogPostTagRelationRepository {
    /**
     * 根据标签ID删除所有关联记录（物理删除）
     *
     * @param tagId 标签ID
     * @return 删除的记录数
     */
    int deleteByTagId(Long tagId);

    /**
     * 根据文章 ID 删除关联
     */
    void deleteByPostId(Long postId);

    /**
     * 批量保存文章-标签关联
     * 
     * @param relations 关联实体列表
     * @return 保存的记录数
     */
    int batchSave(List<BlogPostTagRelation> relations);

    /**
     * 根据文章 ID 查询关联
     */
    List<BlogPostTagRelation> findByPostId(Long postId);
}
