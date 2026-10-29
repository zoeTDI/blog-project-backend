package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostTag;
import cn.caldm.www.post_context.domain.repository.BlogPostTagRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostTagMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostTagPO;
import cn.caldm.www.post_context.infrastructure.persistence.assembler.BlogPostTagAssembler;
import lombok.RequiredArgsConstructor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

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
@Repository
@RequiredArgsConstructor 
public class BlogPostTagRepositoryImpl implements BlogPostTagRepository {

    private final BlogPostTagMapper tagMapper;
    private final BlogPostTagAssembler assembler;

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
        return tagMapper.deleteById(id) == 1;
    }

    @Override
    public Long add(BlogPostTag tag) {
        BlogPostTagPO po = assembler.toPO(tag);
        tagMapper.insert(po);
        return po.getId();
    }

    @Override
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> filtered = ids.stream().filter(Objects::nonNull).toList();
        return tagMapper.deleteBatchIds(filtered);
    }

    @Override
    public List<BlogPostTag> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BlogPostTagPO::getId, ids);
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
                .eq(BlogPostTagPO::getName, name.trim());
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
