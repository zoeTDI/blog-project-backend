package cn.caldm.www.post_context.domain.repository;

import cn.caldm.www.post_context.domain.model.BlogPost;

public interface BlogPostRepository {
    /**
     * 保存文章（包含设置其分类树与标签关联数据）
     *
     * @param blogPost 文章聚合根
     * @return 保存成功后的文章聚合根（包含生成的 ID 等）
     */
    BlogPost save(BlogPost blogPost);
}
