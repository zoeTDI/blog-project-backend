package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPost;

import java.util.List;

public interface BlogPostRepository {
    /**
     * 保存文章（包含设置其分类树与标签关联数据）
     *
     * @param blogPost 文章聚合根
     * @return 保存成功后的文章聚合根（包含生成的 ID 等）
     */
    BlogPost save(BlogPost blogPost);

    /**
     * 根据ID列表批量查询文章（只查询未删除的）
     * @param ids 文章ID列表
     * @return 文章列表（按ID顺序）
     */
    List<BlogPost> findByIds(List<Long> ids);

    /**
     * 根据ID查询单篇文章（只查未删除的）
     * @param id 文章ID
     * @return 文章聚合根
     */
    BlogPost findById(Long id);
}
