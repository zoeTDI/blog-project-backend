package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogCategory;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogCategoryPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 * 博客分类 Assembler
 *
 * @author caldm
 */
@Component
public class BlogCategoryAssembler implements BaseAssembler<BlogCategory, BlogCategoryPO> {

    @Override
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

    @Override
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

}
