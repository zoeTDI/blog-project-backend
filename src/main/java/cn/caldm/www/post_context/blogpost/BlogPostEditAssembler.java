package cn.caldm.www.post_context.blogpost;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostEditAssembler {

    public BlogPostEditDTO toDTO(BlogPost domain) {
        if (domain == null) {
            return null;
        }
        BlogPostEditDTO dto = new BlogPostEditDTO();
        dto.setId(domain.getId());
        dto.setAuthorId(domain.getAuthorId());
        dto.setCreator(domain.getCreator());
        dto.setUpdater(domain.getUpdater());
        dto.setTitle(domain.getTitle());
        dto.setSubtitle(domain.getSubtitle());
        dto.setContentMd(domain.getContentMd());
        dto.setContentHtml(domain.getContentHtml());
        dto.setSummary(domain.getSummary());
        if (domain.getTags() != null) {
            List<Long> tagIds = domain.getTags().stream()
                    .map(BlogPostTag::getId)
                    .collect(Collectors.toList());
            dto.setTags(tagIds);
        } else {
            dto.setTags(new ArrayList<>());
        }
        if (domain.getCategories() != null) {
            dto.setCategories(getIdTree(domain.getCategories()));
        } else {
            dto.setCategories(new ArrayList<>());
        }
        dto.setType(domain.getType());
        dto.setStatus(domain.getStatus());
        dto.setIsTop(domain.getIsTop());
        dto.setIsOriginal(domain.getIsOriginal());
        dto.setCreateTime(domain.getCreateTime());
        dto.setUpdateTime(domain.getUpdateTime());
        dto.setPublishedTime(domain.getPublishedTime());
        dto.setSlug(domain.getSlug());
        dto.setSeoKeywords(domain.getSeoKeywords());
        dto.setSeoDescription(domain.getSeoDescription());
        dto.setPassword(domain.getPassword());
        dto.setAllowComment(domain.getAllowComment());
        dto.setReprintSource(domain.getReprintSource());
        dto.setSortWeight(domain.getSortWeight());
        return dto;
    }

    private List<List<Long>> getIdTree(List<CategoryTreeNode> nodes) {
        List<List<Long>> result = new ArrayList<>();
        for (CategoryTreeNode node : nodes) {
            getPathIds(node, new ArrayDeque<>(), result);
        }
        return result;
    }

    private void getPathIds(CategoryTreeNode node, Deque<Long> stack, List<List<Long>> result) {
        stack.addLast(node.getCategory().getId());
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            result.add(new ArrayList<>(stack));
            stack.removeLast();
            return;
        }
        for (CategoryTreeNode child : node.getChildren()) {
            getPathIds(child, stack, result);
        }
        stack.removeLast();
    }

}
