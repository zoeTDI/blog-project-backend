package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategory;

import java.util.Collection;
import java.util.List;

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

    /**
     * 查询指定用户下的所有可用分类列表
     */
    List<BlogPostCategory> findListByUserId(Long userId);

    /**
     * 根据 ID 软删除分类
     */
    void deleteById(Long id);

    /**
     * 根据 ID 集合批量软删除分类
     */
    void deleteByIds(Collection<Long> ids);

    boolean rename(BlogPostCategory category);
}
