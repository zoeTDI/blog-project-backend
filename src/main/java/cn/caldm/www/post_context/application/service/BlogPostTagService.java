package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.application.service.command.BlogPostTagRenameCommand;
import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRelationRepository;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
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
public class BlogPostTagService {

    private final BlogPostTagRepository tagRepository;
    private final BlogPostTagRelationRepository tagRelationRepository;
    private final CurrentUserProvider currentUserProvider;

    public Long createTag(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            throw new IllegalArgumentException("标签名称不能为空");
        }
        String trimmedName = tagName.trim();
        Long authorId = currentUserProvider.getUserId();
        BlogPostTag existingTag = tagRepository.findByAuthorIdAndName(authorId, trimmedName);
        if (existingTag != null) {
            throw new IllegalArgumentException("标签已存在");
        }
        String currentUser = currentUserProvider.getUsername();
        LocalDateTime now = LocalDateTime.now();
        BlogPostTag tag = new BlogPostTag()
                .setAuthorId(authorId)
                .setName(trimmedName)
                .setCreator(currentUser)
                .setCreateTime(now)
                .setUpdater(currentUser)
                .setUpdateTime(now)
                .setDeleted(false);
        Long tagId = tagRepository.add(tag);
        if (tagId == null) {
            throw new IllegalStateException("创建标签失败");
        }
        return tagId;

    }

    @Transactional(rollbackFor = Exception.class)
    public void renameTag(@Valid BlogPostTagRenameCommand command) {
        Long targetTagId = command.getTargetTagId();
        String newName = command.getNewName().trim();

        Long curUserId = currentUserProvider.getUserId();
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
        existingTag.setUpdater(currentUserProvider.getUsername());
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
        Long curUserId = currentUserProvider.getUserId();
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

}
