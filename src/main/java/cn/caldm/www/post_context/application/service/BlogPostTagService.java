package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.application.service.command.BlogPostTagRenameCommand;
import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.model.BlogPostTagRelation;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import jakarta.validation.Valid;
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

    public Long createTag(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return null;
        }
        String trimmedName = tagName.trim();
        Long authorId = SecurityContextHolder.getUserId();
        BlogPostTag existingTag = tagRepository.findByAuthorIdAndName(authorId, trimmedName);
        if (existingTag != null) {
            return null; // 标签已存在
        }
        if (tagRepository.existsByName(trimmedName, SecurityContextHolder.getUserId())) {
            return null;
        }
        String currentUser = SecurityContextHolder.getUsername();
        LocalDateTime now = LocalDateTime.now();
        BlogPostTag tag = new BlogPostTag()
                .setAuthorId(authorId)
                .setName(trimmedName)
                .setCreator(currentUser)
                .setCreateTime(now)
                .setUpdater(currentUser)
                .setUpdateTime(now)
                .setDeleted(false);
        return tagRepository.add(tag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void renameTag(@Valid BlogPostTagRenameCommand command) {
        Long targetTagId = command.getTargetTagId();
        String newName = command.getNewName().trim();

        Long curUserId = SecurityContextHolder.getUserId();
        BlogPostTag existingTag = tagRepository.findById(targetTagId);
        if (existingTag == null)
            throw new IllegalArgumentException("目标tag不存在");
        if (existingTag.getDeleted())
            throw new IllegalArgumentException("目标tag被软删除");
        if (!existingTag.getAuthorId().equals(curUserId))
            throw new IllegalArgumentException("目标tag不归属当前用户");
        if (existingTag.getName().equals(newName)) {
            // 原名称与旧名称相同，无需更改，直接返回
            return;
        }
        BlogPostTag sameNameTag = tagRepository.findByAuthorIdAndName(curUserId, newName);
        if (sameNameTag != null && !sameNameTag.getId().equals(targetTagId))
            throw new IllegalArgumentException("标签名称已存在");

        existingTag.setName(newName.trim());
        existingTag.setUpdater(SecurityContextHolder.getUsername());
        existingTag.setUpdateTime(LocalDateTime.now());

        tagRepository.update(existingTag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        if (tagId == null) {
            throw new IllegalArgumentException("tag id 为空");
        }

        BlogPostTag tag = tagRepository.findById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("目标tag不存在");
        }
        Long curUserId = SecurityContextHolder.getUserId();
        if (!tag.getAuthorId().equals(curUserId)) {
            throw new IllegalArgumentException("目标tag不归属当前用户");
        }
        if (tag.getDeleted()) {
            // 无需更改，直接返回
            return;
        }
        tagRelationRepository.deleteByTagId(tagId);

        tagRepository.deleteById(tagId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteTags(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return 0;
        }
        tagRelationRepository.deleteByTagIds(tagIds);
        return tagRepository.batchDeleteByIds(tagIds);
    }

    /**
     * 为文章添加单个标签（如果标签不存在则先创建，存在则复用）
     * 
     * @param postId  文章ID
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
     * 
     * @param postId   文章ID
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
     * 
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
     * 
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

    /**
     * 获取创建者的所有标签
     */
    public List<BlogPostTag> getTagsByAuthorId(Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author Id is null.");
        }
        List<BlogPostTag> tags = tagRepository.findByAuthorId(authorId);
        return tags.stream()
                .filter(tag -> tag.getDeleted() == false)
                .collect(Collectors.toList());

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
        Long tagId = tagRepository.add(newTag);
        return tagRepository.findById(tagId);
    }
}
