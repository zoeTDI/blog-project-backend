package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTag;

import java.util.List;

public interface BlogPostTagRepository {
    BlogPostTag findById(Long id);

    boolean update(BlogPostTag tag);

    boolean deleteById(Long id);

    Long add(BlogPostTag tag);

    int batchDeleteByIds(List<Long> ids);

    /**
     * 根据ID列表批量查询标签
     * 
     * @param ids 标签ID列表
     * @return 标签列表（按传入顺序）
     */
    List<BlogPostTag> findByIds(List<Long> ids);

    BlogPostTag findByAuthorIdAndName(Long authorId, String name);

    List<BlogPostTag> findByAuthorId(Long authorId);
}
