package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPost;
import cn.caldm.www.post_context.domain.model.BlogPostStatusEnum;
import cn.caldm.www.post_context.domain.repository.BlogPostRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostPO;
import cn.caldm.www.post_context.infrastructure.persistence.assembler.BlogPostAssembler;
import cn.caldm.www.shared_kernel.domain.PageResult;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
@RequiredArgsConstructor
public class BlogPostRepositoryImpl implements BlogPostRepository {

    private final BlogPostMapper postMapper;
    private final BlogPostAssembler assembler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlogPost save(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }
        BlogPostPO po = assembler.toPO(blogPost);
        postMapper.insert(po);

        blogPost.setId(po.getId());

        return blogPost;
    }

    @Override
    public List<BlogPost> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostPO::getId, ids)
                .eq(BlogPostPO::getDeleted, false);
        return postMapper.selectList(wrapper).stream()
                .map(assembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BlogPost findById(Long id) {
        if (id == null)
            return null;
        BlogPostPO po = postMapper.selectById(id);
        return assembler.toDomain(po);
    }

    @Override
    public PageResult<BlogPost> findPageByAuthorId(Long authorId, long page, long size) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author id cannot be null.");
        }
        Page<BlogPostPO> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<BlogPostPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostPO::getAuthorId, authorId)
                .orderByDesc(BlogPostPO::getIsTop)
                .orderByDesc(BlogPostPO::getSortWeight)
                .orderByDesc(BlogPostPO::getCreateTime)
                .orderByDesc(BlogPostPO::getId);
        Page<BlogPostPO> result = postMapper.selectPage(pageRequest, wrapper);
        List<BlogPost> records = result.getRecords().stream()
                .map(assembler::toDomain)
                .toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }

    @Override
    public Boolean updateById(BlogPost post) {
        if (post == null || post.getId() == null) {
            throw new IllegalArgumentException("Article not existe or article ID not existe.");
        }
        BlogPostPO po = assembler.toPO(post);
        int updateById = postMapper.updateById(po);
        return updateById == 1;
    }

    @Override
    public Boolean updateStatusById(Long postId, BlogPostStatusEnum status, LocalDateTime publishedTime) {
        if (postId == null || status == null) {
            return false;
        }
        LambdaUpdateWrapper<BlogPostPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BlogPostPO::getId, postId)
                .set(BlogPostPO::getStatus, status);
        if (publishedTime != null) {
            wrapper.set(BlogPostPO::getPublishedTime, publishedTime);
        }
        return postMapper.update(null, wrapper) == 1;
    }

    @Override
    public Boolean archiveById(Long postId) {
        if (postId == null) {
            return false;
        }
        return postMapper.deleteById(postId) == 1;
    }
}
