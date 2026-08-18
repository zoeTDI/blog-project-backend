package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTag;

import java.util.List;

public interface BlogPostTagRepository {
    BlogPostTag findById(Long id);
    boolean update(BlogPostTag tag);
    boolean deleteById(Long id);
    boolean add(BlogPostTag tag);
    int batchDeleteByIds(List<Long> ids);
    boolean existsByName(String name, Long authorId);
}
