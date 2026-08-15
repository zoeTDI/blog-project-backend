package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogCategory;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogCategoryPO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 博客分类 Assembler
 * 负责 PO ↔ Domain 的转换
 *
 * @author caldm
 */
@Component
public class BlogCategoryAssembler {

    public BlogCategory toDomain(BlogCategoryPO po) {
        if (po == null) {
            return null;
        }

        BlogCategory domain = new BlogCategory();
        domain.setId(po.getId());
        domain.setUserId(po.getUserId());
        domain.setParentId(po.getParentId());
        domain.setName(po.getName());
        domain.setSlug(po.getSlug());
        domain.setDescription(po.getDescription());
        domain.setSortWeight(po.getSortWeight());
        domain.setStatus(po.getStatus());   // 枚举类型直接赋值
        domain.setCreator(po.getCreator());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdater(po.getUpdater());
        domain.setUpdateTime(po.getUpdateTime());
        domain.setDeleted(po.getDeleted());

        return domain;
    }

    public List<BlogCategory> toDomainList(List<BlogCategoryPO> poList) {
        if (poList == null) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public BlogCategoryPO toPO(BlogCategory domain) {
        if (domain == null) {
            return null;
        }

        BlogCategoryPO po = new BlogCategoryPO();
        po.setId(domain.getId());
        po.setUserId(domain.getUserId());
        po.setParentId(domain.getParentId());
        po.setName(domain.getName());
        po.setSlug(domain.getSlug());
        po.setDescription(domain.getDescription());
        po.setSortWeight(domain.getSortWeight());
        po.setStatus(domain.getStatus());
        po.setCreator(domain.getCreator());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdater(domain.getUpdater());
        po.setUpdateTime(domain.getUpdateTime());
        po.setDeleted(domain.getDeleted());

        return po;
    }

    public List<BlogCategoryPO> toPOList(List<BlogCategory> domainList) {
        if (domainList == null) {
            return Collections.emptyList();
        }
        return domainList.stream()
                .map(this::toPO)
                .collect(Collectors.toList());
    }
}
