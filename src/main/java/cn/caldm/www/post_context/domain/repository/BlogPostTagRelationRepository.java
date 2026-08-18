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
     * 根据标签ID列表批量删除所有关联记录（物理删除）
     *
     * @param tagIds 标签ID列表
     * @return 删除的记录数
     */
    int deleteByTagIds(List<Long> tagIds);

    /**
     * 保存单条文章-标签关联
     * @param relation 关联实体
     * @return 是否保存成功
     */
    boolean save(BlogPostTagRelation relation);

    /**
     * 批量保存文章-标签关联
     * @param relations 关联实体列表
     * @return 保存的记录数
     */
    int batchSave(List<BlogPostTagRelation> relations);

    /**
     * 根据标签ID查询关联的文章ID列表
     * @param tagId 标签ID
     * @return 文章ID列表（可能为空）
     */
    List<Long> findPostIdsByTagId(Long tagId);

    /**
     * 根据标签ID列表查询关联的文章ID列表（去重）
     * @param tagIds 标签ID列表
     * @return 文章ID列表（去重后）
     */
    List<Long> findPostIdsByTagIds(List<Long> tagIds);

    /**
     * 检查某篇文章是否已关联某个标签
     * @param postId 文章ID
     * @param tagId 标签ID
     * @return 是否已关联
     */
    boolean existsByPostIdAndTagId(Long postId, Long tagId);
}
