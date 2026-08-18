package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class BlogPostTagService {
    @Autowired
    private BlogPostTagRepository tagRepository;

    @Autowired
    private BlogPostTagRelationRepository tagRelationRepository;

    @Autowired
    private BlogPostRepository postRepository;

    public boolean createTag(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return false;
        }
        String trimmedName = tagName.trim();
        Long authorId = SecurityContextHolder.getUserId();
        BlogPostTag existingTag = tagRepository.findByAuthorIdAndName(authorId, trimmedName);
        if (existingTag != null) {
            return false; // 标签已存在
        }
        if (tagRepository.existsByName(trimmedName, SecurityContextHolder.getUserId())) {
            return false;
        }
        String currentUser = SecurityContextHolder.getUsername();
        LocalDateTime now = LocalDateTime.now();
        BlogPostTag tag = new BlogPostTag()
                .setName(trimmedName)
                .setCreator(currentUser)
                .setCreateTime(now)
                .setUpdater(currentUser)
                .setUpdateTime(now)
                .setDeleted(false);
        return tagRepository.add(tag);
    }

    public boolean renameTag(Long targetTagId, String newName) {
        if (targetTagId == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }

        BlogPostTag existingTag = tagRepository.findById(targetTagId);
        if (existingTag == null || existingTag.getDeleted()) {
            return false;
        }

        existingTag.setName(newName.trim());
        existingTag.setUpdater(SecurityContextHolder.getUsername());
        existingTag.setUpdateTime(LocalDateTime.now());

        return tagRepository.update(existingTag);
    }

    public boolean deleteTag(Long tagId) {
        if (tagId == null) {
            return false;
        }

        BlogPostTag tag = tagRepository.findById(tagId);
        if (tag == null || tag.getDeleted()) {
            return false;
        }

        tagRelationRepository.deleteByTagId(tagId);

        return tagRepository.deleteById(tagId);
    }

    public int batchDeleteTags(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return 0;
        }
        tagRelationRepository.deleteByTagIds(tagIds);
        return tagRepository.batchDeleteByIds(tagIds);
    }

    /**
     * 为文章添加单个标签（如果标签不存在则先创建，存在则复用）
     * @param postId 文章ID
     * @param tagName 标签名称
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addTagToPost(Long postId, String tagName) {
        if (postId == null || tagName == null || tagName.trim().isEmpty()) {
            return false;
        }

        BlogPost post = postRepository.findById(postId);
        if (post == null) {
            return false;
        }

        BlogPostTag tag = getOrCreateTag(tagName, post.getAuthorId());
        if (tag == null) {
            return false;
        }

        if (tagRelationRepository.existsByPostIdAndTagId(postId, tag.getId())) {
            return true;
        }

        BlogPostTagRelation relation = new BlogPostTagRelation()
                .setPostId(postId)
                .setTagId(tag.getId())
                .setCreateTime(LocalDateTime.now());
        boolean relationSaved = tagRelationRepository.save(relation);

        if (relationSaved) {
            tagRepository.incrementPostCountByIds(List.of(tag.getId()));
        }
        return relationSaved;
    }

    /**
     * 为文章批量添加标签（支持批量创建新标签）
     * @param postId 文章ID
     * @param tagNames 标签名称列表
     * @return 成功添加的标签数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int addTagsToPost(Long postId, List<String> tagNames) {
        if (postId == null || CollectionUtils.isEmpty(tagNames)) {
            return 0;
        }

        BlogPost post = postRepository.findById(postId);
        if (post == null) {
            return 0;
        }

        List<BlogPostTag> tags = new ArrayList<>();
        for (String name : tagNames) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            BlogPostTag tag = getOrCreateTag(name.trim(), post.getAuthorId());
            if (tag != null && tag.getId() != null) {
                tags.add(tag);
            }
        }

        if (tags.isEmpty()) {
            return 0;
        }

        List<BlogPostTag> newTags = tags.stream()
                .filter(t -> !tagRelationRepository.existsByPostIdAndTagId(postId, t.getId()))
                .toList();

        if (newTags.isEmpty()) {
            return 0;
        }

        List<BlogPostTagRelation> relations = newTags.stream()
                .map(tag -> new BlogPostTagRelation()
                        .setPostId(postId)
                        .setTagId(tag.getId())
                        .setCreateTime(LocalDateTime.now()))
                .collect(Collectors.toList());

        int savedCount = tagRelationRepository.batchSave(relations);

        if (savedCount > 0) {
            List<Long> tagIds = newTags.stream()
                    .map(BlogPostTag::getId)
                    .collect(Collectors.toList());
            tagRepository.incrementPostCountByIds(tagIds);
        }

        return savedCount;
    }

    /**
     * 根据标签ID查询关联的文章列表
     * @param tagId 标签ID
     * @return 文章列表
     */
    public List<BlogPost> findPostsByTagId(Long tagId) {
        if (tagId == null) {
            return List.of();
        }

        List<Long> postIds = tagRelationRepository.findPostIdsByTagId(tagId);

        if (postIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findByIds(postIds);
    }

    /**
     * 根据多个标签ID查询关联的文章列表（并集，去重）
     * @param tagIds 标签ID列表
     * @return 文章列表（去重）
     */
    public List<BlogPost> findPostsByTagIds(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return List.of();
        }

        List<Long> postIds = tagRelationRepository.findPostIdsByTagIds(tagIds);

        if (postIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findByIds(postIds);
    }

    private BlogPostTag getOrCreateTag(String tagName, Long authorId) {
        if (authorId == null || !StringUtils.hasText(tagName)) {
            return null;
        }
        String trimmedName = tagName.trim();
        BlogPostTag existingTag = tagRepository.findByAuthorIdAndName(authorId, trimmedName);
        if (existingTag != null) {
            return existingTag;
        }
        String currentUser = SecurityContextHolder.getUsername();
        LocalDateTime now = LocalDateTime.now();
        BlogPostTag newTag = new BlogPostTag()
                .setAuthorId(authorId)
                .setName(trimmedName)
                .setPostCount(0)
                .setCreator(currentUser)
                .setCreateTime(now)
                .setUpdater(currentUser)
                .setUpdateTime(now)
                .setDeleted(false);
        boolean added = tagRepository.add(newTag);
        return added ? newTag : null;
    }
}
