package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.PostCategoryRelation;
import cn.caldm.www.post_context.infrastructure.persistence.po.PostCategoryRelationPO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class PostCategoryRelationAssembler {
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

    public List<PostCategoryRelation> toDomainList(List<PostCategoryRelationPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

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

    public List<PostCategoryRelationPO> toPOList(List<PostCategoryRelation> domainList) {
        if (domainList == null || domainList.isEmpty()) {
            return Collections.emptyList();
        }
        return domainList.stream()
                .map(this::toPO)
                .collect(Collectors.toList());
    }
}
