package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPostCategoryRelation;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryRelationPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostCategoryRelationAssembler implements BaseAssembler<BlogPostCategoryRelation, BlogPostCategoryRelationPO> {

    @Override
    public BlogPostCategoryRelation toDomain(BlogPostCategoryRelationPO po) {
        if (po == null) {
            return null;
        }
        BlogPostCategoryRelation domain = new BlogPostCategoryRelation();
        domain.setId(po.getId());
        domain.setPostId(po.getPostId());
        domain.setCategoryId(po.getCategoryId());
        domain.setIsDirect(po.getIsDirect());
        domain.setDeleted(po.getDeleted());
        return domain;
    }

    @Override
    public BlogPostCategoryRelationPO toPO(BlogPostCategoryRelation domain) {
        if (domain == null) {
            return null;
        }
        BlogPostCategoryRelationPO po = new BlogPostCategoryRelationPO();
        po.setId(domain.getId());
        po.setPostId(domain.getPostId());
        po.setCategoryId(domain.getCategoryId());
        po.setIsDirect(domain.getIsDirect());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}
