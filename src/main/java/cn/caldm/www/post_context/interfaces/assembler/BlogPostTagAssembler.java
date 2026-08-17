package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostTagAssembler implements BaseAssembler<BlogPostTag, BlogPostTagPO> {
    @Override
    public BlogPostTag toDomain(BlogPostTagPO po) {
        if (po == null) {
            return null;
        }
        BlogPostTag domain = new BlogPostTag();
        domain.setId(po.getId());
        domain.setAuthorId(po.getAuthorId());
        domain.setName(po.getName());
        domain.setPostCount(po.getPostCount());
        domain.setCreator(po.getCreator());
        domain.setUpdater(po.getUpdater());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdateTime(po.getUpdateTime());
        domain.setDeleted(po.getDeleted());
        return domain;
    }

    @Override
    public BlogPostTagPO toPO(BlogPostTag domain) {
        if (domain == null) {
            return null;
        }
        BlogPostTagPO po = new BlogPostTagPO();
        po.setId(domain.getId());
        po.setAuthorId(domain.getAuthorId());
        po.setName(domain.getName());
        po.setPostCount(domain.getPostCount());
        po.setCreator(domain.getCreator());
        po.setUpdater(domain.getUpdater());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}
