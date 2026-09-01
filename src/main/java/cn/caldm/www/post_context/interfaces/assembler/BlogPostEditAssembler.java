package cn.caldm.www.post_context.interfaces.assembler;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.post_context.interfaces.dto.BlogPostEditDTO;
import cn.caldm.www.shared_kernel.security.assembler.BaseAssembler;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Component
public class BlogPostEditAssembler implements BaseAssembler<BlogPost, BlogPostEditDTO> {
    @Override
    public BlogPost toDomain(BlogPostEditDTO dto) {
        if (dto == null) {
            return null;
        }
        BlogPost domain = new BlogPost();
        domain.setId(dto.getId());
        domain.setAuthorId(dto.getAuthorId());
        domain.setCreator(dto.getCreator());
        domain.setUpdater(dto.getUpdater());
        domain.setTitle(dto.getTitle());
        domain.setSubtitle(dto.getSubtitle());
        domain.setContentMd(dto.getContentMd());
        domain.setContentHtml(dto.getContentHtml());
        domain.setSummary(dto.getSummary());
        if (dto.getTags() != null) {
            List<BlogPostTag> tags = dto.getTags().stream()
                    .map(id -> {
                        BlogPostTag tag = new BlogPostTag();
                        tag.setId(id);
                        return tag;
                    })
                    .collect(Collectors.toList());
            domain.setTags(tags);
        } else {
            domain.setTags(new ArrayList<>());
        }
        if (dto.getCategories() != null) {
            List<CategoryTreeNode> categories = dto.getCategories().stream()
                    .filter(Objects::nonNull)
                    .filter(path -> !path.isEmpty())
                    .map(path -> {
                        Long categoryId = path.get(path.size() - 1);
                        CategoryTreeNode node = new CategoryTreeNode();
                        node.setCategory(new BlogPostCategory().setId(categoryId));
                        return node;
                    })
                    .collect(Collectors.toList());
            domain.setCategories(categories);
        } else {
            domain.setCategories(new ArrayList<>());
        }
        domain.setType(dto.getType());
        domain.setStatus(dto.getStatus());
        domain.setIsTop(dto.getIsTop());
        domain.setIsOriginal(dto.getIsOriginal());
        domain.setCreateTime(dto.getCreateTime());
        domain.setUpdateTime(dto.getUpdateTime());
        domain.setPublishedTime(dto.getPublishedTime());
        domain.setSlug(dto.getSlug());
        domain.setSeoKeywords(dto.getSeoKeywords());
        domain.setSeoDescription(dto.getSeoDescription());
        domain.setPassword(dto.getPassword());
        domain.setAllowComment(dto.getAllowComment());
        domain.setReprintSource(dto.getReprintSource());
        domain.setSortWeight(dto.getSortWeight());
        // 以下字段在 DTO 中不存在，设置默认值
        domain.setViews(0);
        domain.setLikes(0);
        domain.setCollects(0);
        domain.setCommentCount(0);
        domain.setDeleted(false);
        return domain;
    }

    @Override
    public BlogPostEditDTO toPO(BlogPost domain) {
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
            getPathIds(node, new ArrayStack<>(7), result);
        }
        return result;
    }

    private List<Long> getPathIds(CategoryTreeNode node, ArrayStack<Long> stack, List<List<Long>> result) {
        stack.add(node.getCategory().getId());
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            result.add(new ArrayList<>(stack));
            stack.pop();
            return stack;
        }
        for (CategoryTreeNode child : node.getChildren()) {
            getPathIds(child, stack, result);
        }
        stack.pop();
        return stack;
    }

}
