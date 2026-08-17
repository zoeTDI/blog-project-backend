package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
import cn.caldm.www.post_context.application.service.command.CategoryNodeParam;
import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.CategoryTreeNode;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Service
@RequiredArgsConstructor
public class BlogPostService {
    @Autowired
    private BlogPostRepository blogPostRepository;

    public Long createPost(BlogPostCreateCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("创建文章参数不能为空");
        }

        BlogPost blogPost = new BlogPost();
        blogPost.setAuthorId(command.getAuthorId());
        blogPost.setCreator(command.getCreator());
        blogPost.setUpdater(command.getCreator());
        blogPost.setTitle(command.getTitle());
        blogPost.setSubtitle(command.getSubtitle());
        blogPost.setContentMd(command.getContentMd());
        blogPost.setContentHtml(command.getContentHtml());
        blogPost.setSummary(command.getSummary());
        blogPost.setType(command.getType());
        blogPost.setStatus(command.getStatus());
        blogPost.setIsTop(command.getIsTop());
        blogPost.setIsOriginal(command.getIsOriginal());
        blogPost.setPublishedTime(command.getPublishedTime());
        blogPost.setSlug(command.getSlug());
        blogPost.setSeoKeywords(command.getSeoKeywords());
        blogPost.setSeoDescription(command.getSeoDescription());
        blogPost.setPassword(command.getPassword());
        blogPost.setAllowComment(command.getAllowComment());
        blogPost.setReprintSource(command.getReprintSource());
        blogPost.setSortWeight(command.getSortWeight());
        blogPost.setCreateTime(LocalDateTime.now());
        blogPost.setUpdateTime(LocalDateTime.now());
        blogPost.setViews(0);
        blogPost.setLikes(0);
        blogPost.setCollects(0);
        blogPost.setCommentCount(0);

        if (command.getTagIds() != null && !command.getTagIds().isEmpty()) {
            List<BlogPostTag> tags = command.getTagIds().stream()
                    .map(tagId -> {
                        BlogPostTag tag = new BlogPostTag();
                        tag.setId(tagId);
                        return tag;
                    })
                    .collect(Collectors.toList());
            blogPost.assignTags(tags);
        }

        if (command.getCategoryTrees() != null && !command.getCategoryTrees().isEmpty()) {
            List<CategoryTreeNode> categoryTrees = command.getCategoryTrees().stream()
                    .map(this::convertToCategoryTreeNode)
                    .collect(Collectors.toList());
            blogPost.assignCategories(categoryTrees);
        }

        BlogPost savedPost = blogPostRepository.save(blogPost);
        return savedPost != null ? savedPost.getId() : null;
    }

    /**
     * 递归转换 Command 的分类树参数为领域模型 CategoryTreeNode
     */
    private CategoryTreeNode convertToCategoryTreeNode(CategoryNodeParam param) {
        if (param == null || param.getCategoryId() == null) {
            return null;
        }

        BlogPostCategory category = new BlogPostCategory();
        category.setId(param.getCategoryId());

        CategoryTreeNode node = new CategoryTreeNode();
        node.setCategory(category);

        if (param.getChildren() != null && !param.getChildren().isEmpty()) {
            List<CategoryTreeNode> children = param.getChildren().stream()
                    .map(this::convertToCategoryTreeNode)
                    .filter(child -> child != null)
                    .collect(Collectors.toList());
            node.setChildren(children);
        } else {
            node.setChildren(new ArrayList<>());
        }

        return node;
    }
}
