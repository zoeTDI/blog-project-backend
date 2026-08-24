package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostTagAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @author caldm
 */
@Repository
public class BlogPostTagRepositoryImpl implements BlogPostTagRepository {
    @Autowired
    private BlogPostTagMapper tagMapper;
    @Autowired
    private BlogPostTagAssembler assembler;

    @Override
    public BlogPostTag findById(Long id) {
        BlogPostTagPO po = tagMapper.selectById(id);
        return assembler.toDomain(po);
    }

    @Override
    public boolean update(BlogPostTag tag) {
        BlogPostTagPO po = assembler.toPO(tag);
        return tagMapper.updateById(po) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BlogPostTagPO::getId, id).set(BlogPostTagPO::getDeleted, true);
        return tagMapper.update(wrapper) == 1;
    }

    @Override
    public Long add(BlogPostTag tag) {
        BlogPostTagPO po = assembler.toPO(tag);
        tagMapper.insert(po);
        return po.getId();
    }

    @Override
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return 0;
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(BlogPostTagPO::getId, ids)
                .eq(BlogPostTagPO::getDeleted, false)
                .set(BlogPostTagPO::getDeleted, true);
        return tagMapper.update(null, wrapper);
    }

    @Override
    public boolean existsByName(String name, Long authorId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagPO::getName, name.trim())
                .eq(BlogPostTagPO::getAuthorId, authorId);
        return tagMapper.exists(wrapper);
    }

    @Override
    public int incrementPostCountByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return 0;
        }
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(BlogPostTagPO::getId, tagIds)
                .setSql("post_count = post_count + 1");
        return tagMapper.update(null, wrapper);
    }

    @Override
    public int decrementPostCountByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return 0;
        }
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(BlogPostTagPO::getId, tagIds)
                .gt(BlogPostTagPO::getPostCount, 0)
                .setSql("post_count = post_count - 1");
        return tagMapper.update(null, wrapper);
    }

    @Override
    public List<BlogPostTag> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostTagPO::getId, ids)
                .eq(BlogPostTagPO::getDeleted, false);
        return tagMapper.selectList(wrapper).stream()
                .map(assembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BlogPostTag findByAuthorIdAndName(Long authorId, String name) {
        if (authorId == null || name == null || name.trim().isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagPO::getAuthorId, authorId)
                .eq(BlogPostTagPO::getName, name.trim())
                .eq(BlogPostTagPO::getDeleted, false);
        BlogPostTagPO po = tagMapper.selectOne(wrapper);
        return assembler.toDomain(po);
    }

    @Override
    public List<BlogPostTag> findByAuthorId(Long authorId) {
        if (authorId == null) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagPO::getAuthorId, authorId);
        List<BlogPostTagPO> selectList = tagMapper.selectList(wrapper);
        if (selectList == null || selectList.isEmpty()) {
            return List.of();
        }
        return assembler.toDomainList(selectList);
    }

}
