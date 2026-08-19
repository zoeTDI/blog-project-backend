package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTag;

import java.util.List;

public interface BlogPostTagRepository {
    BlogPostTag findById(Long id);
    boolean update(BlogPostTag tag);
    boolean deleteById(Long id);
    Long add(BlogPostTag tag);
    int batchDeleteByIds(List<Long> ids);
    boolean existsByName(String name, Long authorId);

    /**
     * 批量增加标签的关联文章数量（postCount +1）
     * @param tagIds 标签ID列表
     * @return 更新的记录数
     */
    int incrementPostCountByIds(List<Long> tagIds);

    /**
     * 批量减少标签的关联文章数量（postCount -1）
     * @param tagIds 标签ID列表
     * @return 更新的记录数
     */
    int decrementPostCountByIds(List<Long> tagIds);

    /**
     * 根据ID列表批量查询标签
     * @param ids 标签ID列表
     * @return 标签列表（按传入顺序）
     */
    List<BlogPostTag> findByIds(List<Long> ids);

    BlogPostTag findByAuthorIdAndName(Long authorId, String name);
}
