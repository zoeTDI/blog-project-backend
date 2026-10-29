package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.application.service.command.BlogPostCreateCommand;
import cn.caldm.www.post_context.application.service.command.BlogPostUpdateCommand;
import cn.caldm.www.post_context.domain.model.*;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.post_context.utils.BlogPostCategoryUtils;
import cn.caldm.www.shared_kernel.domain.PageResult;
import cn.caldm.www.shared_kernel.domain.ResourceNotFoundException;
import cn.caldm.www.shared_kernel.domain.ResultCodeEnum;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Validated
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostCategoryRelationRepository categoryRelationRepository;
    private final BlogPostTagRelationRepository tagRelationRepository;
    private final BlogPostCategoryRepository categoryRepository;
    private final BlogPostTagRepository tagRepository;
    private final CurrentUserProvider currentUserProvider;

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
        return blogPostRepository.findPageByAuthorId(currentUserProvider.getUserId(), page, size);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createPost(@Valid BlogPostCreateCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("创建文章参数不能为空");
        }

        BlogPost blogPost = new BlogPost();
        Long curUserId = currentUserProvider.getUserId();
        String curUsername = currentUserProvider.getUsername();
        blogPost.setAuthorId(curUserId);
        blogPost.setCreator(curUsername);
        blogPost.setUpdater(curUsername);
        blogPost.setTitle(command.getTitle());
        blogPost.setSubtitle(command.getSubtitle());
        blogPost.setContentMd(command.getContentMd());
        blogPost.setContentHtml(command.getContentHtml());
        blogPost.setSummary(command.getSummary());
        blogPost.setType(command.getType());
        blogPost.setStatus(BlogPostStatusEnum.DRAFT);
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
        if (savedPost == null || savedPost.getId() == null) {
            throw new IllegalStateException("创建文章失败");
        }
        Long postId = savedPost.getId();
        if (command.getTagIds() != null && !command.getTagIds().isEmpty()) {
            List<BlogPostTagRelation> tagRelations = command.getTagIds().stream()
                    .filter(Objects::nonNull)
                    .map(id -> new BlogPostTagRelation().setPostId(postId).setTagId(id))
                    .toList();
            tagRelationRepository.batchSave(tagRelations);
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
        if (blogPost == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "Blog post isn't exist.");
        }
        if (!isAuthorOrAdmin(blogPost)) {
            throw new IllegalArgumentException("没有权限查看该文章");
        }
        // set category
        List<BlogPostCategory> categoryList = getCategoryListByPostId(id);
        List<CategoryTreeNode> categoryTree = BlogPostCategoryUtils.buildTree(categoryList);
        blogPost.setCategories(categoryTree);
        // set tag
        blogPost.setTags(getTagListByPostId(id));
        return blogPost;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBlogPost(@Validated BlogPostUpdateCommand command) {
        BlogPost post = blogPostRepository.findById(command.getTargetPostId());
        if (post == null) {
            throw new IllegalArgumentException("Target post is null.");
        }
        if (BlogPostStatusEnum.REVIEWING.equals(post.getStatus())) {
            throw new IllegalArgumentException("Target article is under review, cannot modify.");
        }
        Long postId = post.getId();
        String curUsername = currentUserProvider.getUsername();
        if (!isAuthorOrAdmin(post)) {
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
        newPost.setIsTop(command.getIsTop());
        newPost.setIsOriginal(command.getIsOriginal());
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
                .map(BlogPostTagRelation::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return tagRepository.findByIds(tagIds);
    }

    public void toReview(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Target post id is null.");
        }
        BlogPost cached = blogPostRepository.findById(postId);
        Long userId = currentUserProvider.getUserId();
        List<String> roles = currentUserProvider.getRoles();
        Boolean isAuthor = cached.getAuthorId().equals(userId) && roles.contains("AUTHOR");
        Boolean isAdmin = roles.contains("ADMIN");
        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("No permission to do it.");
        }
        cached.submitForReview();
        blogPostRepository.updateStatusById(postId, cached.getStatus(), cached.getPublishedTime());
    }

    public void toPublished(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Target post id is null.");
        }
        BlogPost cached = blogPostRepository.findById(postId);
        List<String> roles = currentUserProvider.getRoles();
        if (!roles.contains("ADMIN") && !roles.contains("AUDITOR")) {
            throw new IllegalStateException("No permission to do it.");
        }
        cached.approve();
        blogPostRepository.updateStatusById(postId, cached.getStatus(), cached.getPublishedTime());
    }

    public void softDeletePost(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Target post id is null.");
        }
        BlogPost cached = blogPostRepository.findById(postId);
        if (cached == null) {
            throw new ResourceNotFoundException(ResultCodeEnum.NOT_FOUND, "目标文章不存在");
        }
        if (!isAuthorOrAdmin(cached)) {
            throw new IllegalArgumentException("没有权限删除该文章");
        }
        cached.archive();
        if (!blogPostRepository.archiveById(postId)) {
            throw new IllegalStateException("删除文章失败");
        }
    }

    private boolean isAuthorOrAdmin(BlogPost post) {
        Long curUserId = currentUserProvider.getUserId();
        List<String> roles = currentUserProvider.getRoles();
        boolean isAuthor = Objects.equals(post.getAuthorId(), curUserId);
        boolean isAdmin = roles != null && roles.contains("ADMIN");
        return isAuthor || isAdmin;
    }
}
