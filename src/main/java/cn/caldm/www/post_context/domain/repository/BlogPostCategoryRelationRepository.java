package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;

import java.util.Collection;
import java.util.List;

public interface BlogPostCategoryRelationRepository {
    /**
     * 单条保存文章-分类关联
     */
    BlogPostCategoryRelation save(BlogPostCategoryRelation relation);

    /**
     * 批量保存文章-分类关联
     */
    void batchSave(List<BlogPostCategoryRelation> relations);

    /**
     * 按分类 ID 删除所有关联
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 按分类 ID 集合批量删除关联
     */
    void deleteByCategoryIds(Collection<Long> categoryIds);

    /**
     * 按文章 ID 删除关联
     */
    void deleteByPostId(Long postId);

    /**
     * 按文章 ID 和分类 ID 集合解绑关联
     */
    void deleteByPostIdAndCategoryIds(Long postId, Collection<Long> categoryIds);

    /**
     * 根据分类 ID 集合及关联类型（直接/间接）查询关联记录
     */
    List<BlogPostCategoryRelation> findByCategoryIdsAndIsDirect(Collection<Long> categoryIds, Boolean isDirect);

    /**
     * 根据文章 ID 集合及关联类型（直接/间接）查询关联记录
     */
    List<BlogPostCategoryRelation> findByPostIdsAndIsDirect(Collection<Long> postIds, Boolean isDirect);

    /**
     * 根据文章 ID 集合及关联类型（直接/间接）批量删除关联记录
     */
    void deleteByPostIdsAndIsDirect(Collection<Long> postIds, Boolean isDirect);

    /**
     * 根据文章 ID 获取关联
     */
    List<BlogPostCategoryRelation> findByPostId(Long postId);
}
