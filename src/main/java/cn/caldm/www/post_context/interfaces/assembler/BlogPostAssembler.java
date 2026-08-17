package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostPO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostAssembler implements BaseAssembler<BlogPost, BlogPostPO> {
    @Override
    public BlogPost toDomain(BlogPostPO po) {
        if (po == null) {
            return null;
        }
        BlogPost domain = new BlogPost();
        domain.setId(po.getId());
        domain.setAuthorId(po.getAuthorId());
        domain.setCreator(po.getCreator());
        domain.setUpdater(po.getUpdater());
        domain.setTitle(po.getTitle());
        domain.setSubtitle(po.getSubtitle());
        domain.setContentMd(po.getContentMd());
        domain.setContentHtml(po.getContentHtml());
        domain.setSummary(po.getSummary());
        domain.setTags(po.getTags());
        domain.setType(po.getType());
        domain.setStatus(po.getStatus());
        domain.setIsTop(po.getIsTop());
        domain.setIsOriginal(po.getIsOriginal());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdateTime(po.getUpdateTime());
        domain.setPublishedTime(po.getPublishedTime());
        domain.setViews(po.getViews());
        domain.setLikes(po.getLikes());
        domain.setCollects(po.getCollects());
        domain.setCommentCount(po.getCommentCount());
        domain.setSlug(po.getSlug());
        domain.setSeoKeywords(po.getSeoKeywords());
        domain.setSeoDescription(po.getSeoDescription());
        domain.setPassword(po.getPassword());
        domain.setAllowComment(po.getAllowComment());
        domain.setReprintSource(po.getReprintSource());
        domain.setSortWeight(po.getSortWeight());
        domain.setDeleted(po.getDeleted());
        return domain;
    }

    @Override
    public BlogPostPO toPO(BlogPost domain) {
        if (domain == null) {
            return null;
        }
        BlogPostPO po = new BlogPostPO();
        po.setId(domain.getId());
        po.setAuthorId(domain.getAuthorId());
        po.setCreator(domain.getCreator());
        po.setUpdater(domain.getUpdater());
        po.setTitle(domain.getTitle());
        po.setSubtitle(domain.getSubtitle());
        po.setContentMd(domain.getContentMd());
        po.setContentHtml(domain.getContentHtml());
        po.setSummary(domain.getSummary());
        po.setTags(domain.getTags());
        po.setType(domain.getType());
        po.setStatus(domain.getStatus());
        po.setIsTop(domain.getIsTop());
        po.setIsOriginal(domain.getIsOriginal());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        po.setPublishedTime(domain.getPublishedTime());
        po.setViews(domain.getViews());
        po.setLikes(domain.getLikes());
        po.setCollects(domain.getCollects());
        po.setCommentCount(domain.getCommentCount());
        po.setSlug(domain.getSlug());
        po.setSeoKeywords(domain.getSeoKeywords());
        po.setSeoDescription(domain.getSeoDescription());
        po.setPassword(domain.getPassword());
        po.setAllowComment(domain.getAllowComment());
        po.setReprintSource(domain.getReprintSource());
        po.setSortWeight(domain.getSortWeight());
        po.setDeleted(domain.getDeleted());
        return po;
    }
}
