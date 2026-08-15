package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.PostCategoryRelation;
import cn.caldm.www.post_context.infrastructure.persistence.po.PostCategoryRelationPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class PostCategoryRelationAssembler implements BaseAssembler<PostCategoryRelation, PostCategoryRelationPO> {

    @Override
    public PostCategoryRelation toDomain(PostCategoryRelationPO po) {
        if (po == null) {
            return null;
        }
        PostCategoryRelation domain = new PostCategoryRelation();
        domain.setId(po.getId());
        domain.setPostId(po.getPostId());
        domain.setCategoryId(po.getCategoryId());
        domain.setIsDirect(po.getIsDirect());
        domain.setDeleted(po.getDeleted());
        return domain;
    }

    @Override
    public PostCategoryRelationPO toPO(PostCategoryRelation domain) {
        if (domain == null) {
            return null;
        }
        PostCategoryRelationPO po = new PostCategoryRelationPO();
        po.setId(domain.getId());
        po.setPostId(domain.getPostId());
        po.setCategoryId(domain.getCategoryId());
        po.setIsDirect(domain.getIsDirect());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}
