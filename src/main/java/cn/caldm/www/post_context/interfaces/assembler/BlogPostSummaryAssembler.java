package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.interfaces.dto.BlogPostSummaryDTO;
import org.springframework.stereotype.Component;

@Component
public class BlogPostSummaryAssembler {
    public BlogPostSummaryDTO toDTO(BlogPost post) {
        BlogPostSummaryDTO dto = new BlogPostSummaryDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSubtitle(post.getSubtitle());
        dto.setSummary(post.getSummary());
        dto.setType(post.getType());
        dto.setStatus(post.getStatus());
        dto.setIsTop(post.getIsTop());
        dto.setIsOriginal(post.getIsOriginal());
        dto.setCreateTime(post.getCreateTime());
        dto.setUpdateTime(post.getUpdateTime());
        dto.setPublishedTime(post.getPublishedTime());
        dto.setViews(post.getViews());
        dto.setLikes(post.getLikes());
        dto.setCollects(post.getCollects());
        dto.setCommentCount(post.getCommentCount());
        dto.setSlug(post.getSlug());
        dto.setAllowComment(post.getAllowComment());
        dto.setSortWeight(post.getSortWeight());
        return dto;
    }
}
