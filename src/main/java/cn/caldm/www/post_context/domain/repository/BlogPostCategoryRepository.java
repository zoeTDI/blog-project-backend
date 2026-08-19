package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategory;

public interface BlogPostCategoryRepository {
    /**
     * 校验同一用户下是否存在同名分类
     */
    boolean existsByUserIdAndName(Long userId, String name);

    /**
     * 根据 ID 查询分类
     */
    BlogPostCategory findById(Long id);

    /**
     * 保存/更新分类
     */
    BlogPostCategory save(BlogPostCategory category);
}
