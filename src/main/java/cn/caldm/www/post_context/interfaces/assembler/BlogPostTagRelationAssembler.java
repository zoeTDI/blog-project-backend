package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagRelationPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostTagRelationAssembler implements BaseAssembler<BlogPostTagRelation, BlogPostTagRelationPO> {
    @Override
    public BlogPostTagRelation toDomain(BlogPostTagRelationPO po) {
        if (po == null) {
            return null;
        }
        BlogPostTagRelation domain = new BlogPostTagRelation();
        domain.setId(po.getId());
        domain.setPostId(po.getPostId());
        domain.setTagId(po.getTagId());
        domain.setCreateTime(po.getCreateTime());
        return domain;
    }

    @Override
    public BlogPostTagRelationPO toPO(BlogPostTagRelation domain) {
        if (domain == null) {
            return null;
        }
        BlogPostTagRelationPO po = new BlogPostTagRelationPO();
        po.setId(domain.getId());
        po.setPostId(domain.getPostId());
        po.setTagId(domain.getTagId());
        po.setCreateTime(domain.getCreateTime());
        return po;
    }
}
