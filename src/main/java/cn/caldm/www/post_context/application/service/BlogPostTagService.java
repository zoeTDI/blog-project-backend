package cn.caldm.www.post_context.application.service;

import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.shared_kernel.security.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 *
 *
 * @author caldm
 */
@Service
public class BlogPostTagService {
    @Autowired
    private BlogPostTagRepository blogPostTagRepository;

    public boolean createTag(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return false;
        }
        String trimmedName = tagName.trim();
        if (blogPostTagRepository.existsByName(trimmedName, SecurityContextHolder.getUserId())) {
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
        return blogPostTagRepository.add(tag);
    }

    public boolean renameTag(Long targetTagId, String newName) {
        if (targetTagId == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }

        BlogPostTag existingTag = blogPostTagRepository.findById(targetTagId);
        if (existingTag == null || existingTag.getDeleted()) {
            return false;
        }

        existingTag.setName(newName.trim());
        existingTag.setUpdater(SecurityContextHolder.getUsername());
        existingTag.setUpdateTime(LocalDateTime.now());

        return blogPostTagRepository.update(existingTag);
    }

    public boolean deleteTag(Long tagId) {
        if (tagId == null) {
            return false;
        }

        BlogPostTag tag = blogPostTagRepository.findById(tagId);
        if (tag == null || tag.getDeleted()) {
            return false;
        }
        return blogPostTagRepository.deleteById(tagId);
    }

    public int batchDeleteTags(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return 0;
        }
        return blogPostTagRepository.batchDeleteByIds(tagIds);
    }

}
