package cn.caldm.www.post_context.application.service;

import cn.caldm.www.common.domain.PageResult;
import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostUpdateCommand;
import cn.caldm.www.post_context.domain.model.*;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.post_context.utils.BlogPostCategoryUtils;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import cn.caldm.www.user_context.domain.modal.RoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private BlogPostCategoryRelationRepository categoryRelationRepository;

    @Autowired
    private BlogPostTagRelationRepository tagRelationRepository;

    @Autowired
    private BlogPostCategoryRepository categoryRepository;

    @Autowired
    private BlogPostTagRepository tagRepository;

    /**
     * Queries the complete (all statuses), non-deleted article list of the
     * authenticated user.
     */
    public PageResult<BlogPost> getCurrentUserPosts(long page, long size) {
        if (page < 1) {
            throw new IllegalArgumentException("page must be greater than or equal to 1");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
        return blogPostRepository.findPageByAuthorId(SecurityContextHolder.getUserId(), page, size);
    }

    public Long createPost(@Valid BlogPostCreateCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("创建文章参数不能为空");
        }

        BlogPost blogPost = new BlogPost();
        Long curUserId = SecurityContextHolder.getUserId();
        String curUsername = SecurityContextHolder.getUsername();
        blogPost.setAuthorId(curUserId);
        blogPost.setCreator(curUsername);
        blogPost.setUpdater(curUsername);
        blogPost.setTitle(command.getTitle());
        blogPost.setSubtitle(command.getSubtitle());
        blogPost.setContentMd(command.getContentMd());
        blogPost.setContentHtml(command.getContentHtml());
        blogPost.setSummary(command.getSummary());
        blogPost.setType(command.getType());
        blogPost.setStatus(command.getStatus());
        blogPost.setIsTop(command.getIsTop());
        blogPost.setIsOriginal(command.getIsOriginal());
        blogPost.setPublishedTime(null);
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

        BlogPost savedPost = blogPostRepository.save(blogPost);
        if (savedPost == null) {
            return null;
        }
        Long postId = savedPost.getId();
        if (postId == null) {
            return null;
        }
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
        List<List<Long>> categoryTrees = command.getCategoryTrees();
        if (categoryTrees != null && !categoryTrees.isEmpty()) {
            List<BlogPostCategoryRelation> relations = categoryTrees.stream()
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .map(categoryId -> new BlogPostCategoryRelation()
                            .setPostId(postId)
                            .setCategoryId(categoryId)
                            .setIsDirect(true))
                    .toList();
            categoryRelationRepository.batchSave(relations);
        }
        return postId;
    }

    public BlogPost getBlogPostById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Blog post id must be not null.");
        }
        BlogPost blogPost = blogPostRepository.findById(id);
        if (blogPost == null || blogPost.getDeleted()) {
            throw new IllegalArgumentException("Blog post is already deleted.");
        }
        Long curUserId = SecurityContextHolder.getUserId();
        List<RoleEnum> curRoles = SecurityContextHolder.getRoles();
        if (!blogPost.getAuthorId().equals(curUserId) || curRoles == null || !curRoles.contains(RoleEnum.ADMIN)) {
            throw new IllegalArgumentException("Current user is not author.");
        }
        // set category
        List<BlogPostCategory> categoryList = getCategoryListByPostId(id);
        List<CategoryTreeNode> categoryTree = BlogPostCategoryUtils.buildTree(categoryList);
        blogPost.setCategories(categoryTree);
        // set tag
        blogPost.setTags(getTagListByPostId(id));
        return blogPost;
    }

    public void updateBlogPost(@Validated BlogPostUpdateCommand command) {
        BlogPost post = blogPostRepository.findById(command.getTargetPostId());
        if (post == null) {
            throw new IllegalArgumentException("Target post is null.");
        }
        if (post.getDeleted()) {
            throw new IllegalArgumentException("Target post is deleted.");
        }
        if (BlogPostStatusEnum.REVIEWING.equals(post.getStatus())) {
            throw new IllegalArgumentException("Target article is under review, cannot modify.");
        }
        Long postId = post.getId();
        Long curUserId = SecurityContextHolder.getUserId();
        String curUsername = SecurityContextHolder.getUsername();
        List<RoleEnum> roles = SecurityContextHolder.getRoles();
        Boolean isAuthor = post.getAuthorId().equals(curUserId);
        Boolean isAdmin = roles != null && roles.contains(RoleEnum.ADMIN);
        if (!isAuthor || !isAdmin) {
            throw new IllegalArgumentException("No permission to update post.");
        }
        BlogPost newPost = new BlogPost();
        newPost.setId(postId);
        newPost.setTitle(command.getTitle());
        newPost.setSubtitle(command.getSubtitle());
        newPost.setContentMd(command.getContentMd());
        newPost.setContentHtml(command.getContentHtml());
        newPost.setSummary(command.getSummary());
        newPost.setType(command.getType());
        newPost.setStatus(command.getStatus());
        newPost.setIsTop(command.getIsTop());
        newPost.setIsOriginal(command.getIsOriginal());
        newPost.setPublishedTime(command.getPublishedTime());
        newPost.setSlug(command.getSlug());
        newPost.setSeoKeywords(command.getSeoKeywords());
        newPost.setSeoDescription(command.getSeoDescription());
        newPost.setAllowComment(command.getAllowComment());
        newPost.setReprintSource(command.getReprintSource());
        newPost.setSortWeight(command.getSortWeight());
        newPost.setUpdater(curUsername);
        newPost.setUpdateTime(LocalDateTime.now());

        Boolean updateById = blogPostRepository.updateById(newPost);
        if (!updateById) {
            throw new IllegalStateException(
                    "Failed to update blog post, possibly due to concurrent modification or data inconsistency.");
        }
        // 更新分类关联
        categoryRelationRepository.deleteByPostId(postId);
        List<List<Long>> categoryIds = command.getCategoryIds();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Long> flatList = categoryIds.stream()
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            List<BlogPostCategoryRelation> postCategoryRelations = flatList.stream()
                    .map(id -> new BlogPostCategoryRelation().setPostId(postId).setCategoryId(id))
                    .collect(Collectors.toList());
            categoryRelationRepository.batchSave(postCategoryRelations);
        }
        // 更新标签关联
        tagRelationRepository.deleteByPostId(postId);
        List<Long> tagIds = command.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Long> filtered = tagIds.stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<BlogPostTagRelation> tagRelations = filtered.stream()
                    .map(id -> new BlogPostTagRelation().setPostId(postId).setTagId(id))
                    .collect(Collectors.toList());
            tagRelationRepository.batchSave(tagRelations);
        }
    }

    /**
     * 根据文章 ID 获取关联分类
     */
    public List<BlogPostCategory> getCategoryListByPostId(Long postId) {
        if (postId == null) {
            return new ArrayList<>();
        }
        List<BlogPostCategoryRelation> relations = categoryRelationRepository.findByPostId(postId);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> categoryIds = relations.stream()
                .map(BlogPostCategoryRelation::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return categoryRepository.batchFindById(categoryIds);
    }

    /**
     * 根据文章 ID 获取关联标签
     */
    public List<BlogPostTag> getTagListByPostId(Long postId) {
        if (postId == null) {
            return new ArrayList<>();
        }
        List<BlogPostTagRelation> relations = tagRelationRepository.findByPostId(postId);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> tagIds = relations.stream()
                .map(BlogPostTagRelation::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return tagRepository.findByIds(tagIds);
    }

}
