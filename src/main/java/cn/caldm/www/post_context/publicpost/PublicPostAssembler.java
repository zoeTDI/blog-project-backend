package cn.caldm.www.post_context.publicpost;

import org.springframework.stereotype.Component;

import cn.caldm.www.post_context.domain.model.BlogPost;

@Component 
public class PublicPostAssembler {

    public PublicPostDTO toDto(BlogPost domain) {
        if (domain == null) {
            return null;
        }

        PublicPostDTO dto = new PublicPostDTO();
        dto.setId(domain.getId());
        dto.setAuthorId(domain.getAuthorId());
        dto.setAuthor(domain.getCreator());
        dto.setTitle(domain.getTitle());
        dto.setSubtitle(domain.getSubtitle());
        dto.setContentMd(domain.getContentMd());
        dto.setContentHtml(domain.getContentHtml());
        dto.setSummary(domain.getSummary());
        dto.setType(domain.getType());
        dto.setIsTop(domain.getIsTop());
        dto.setIsOriginal(domain.getIsOriginal());
        dto.setCreateTime(domain.getCreateTime());
        dto.setPublishedTime(domain.getPublishedTime());
        dto.setViews(domain.getViews());
        dto.setLikes(domain.getLikes());
        dto.setCollects(domain.getCollects());
        dto.setCommentCount(domain.getCommentCount());
        dto.setSlug(domain.getSlug());
        dto.setSeoKeywords(domain.getSeoKeywords());
        dto.setSeoDescription(domain.getSeoDescription());
        dto.setAllowComment(domain.getAllowComment());
        dto.setReprintSource(domain.getReprintSource());
        dto.setTags(domain.getTags());
        dto.setCategories(domain.getCategories());
        return dto;
    }
}
