package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostCategoryMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostCategoryAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
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
public class BlogPostCategoryRepositoryImpl implements BlogPostCategoryRepository {
    @Autowired
    private BlogPostCategoryMapper categoryMapper;

    @Autowired
    private BlogPostCategoryAssembler categoryAssembler;

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        LambdaQueryWrapper<BlogPostCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryPO::getUserId, userId)
                .eq(BlogPostCategoryPO::getName, name);
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public BlogPostCategory findById(Long id) {
        if (id == null) {
            return null;
        }
        BlogPostCategoryPO po = categoryMapper.selectById(id);
        return categoryAssembler.toDomain(po);
    }

    @Override
    public List<BlogPostCategory> findListByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BlogPostCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryPO::getUserId, userId);
        List<BlogPostCategoryPO> pos = categoryMapper.selectList(wrapper);
        return pos.stream().map(categoryAssembler::toDomain).collect(Collectors.toList());
    }

    @Override
    public BlogPostCategory save(BlogPostCategory category) {
        BlogPostCategoryPO po = categoryAssembler.toPO(category);
        if (po.getId() == null) {
            categoryMapper.insert(po);
        } else {
            categoryMapper.updateById(po);
        }
        return categoryAssembler.toDomain(po);
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            categoryMapper.deleteById(id);
        }
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            categoryMapper.deleteBatchIds(ids);
        }
    }

    @Override
    public boolean rename(BlogPostCategory category) {
        BlogPostCategoryPO po = categoryAssembler.toPO(category);
        return categoryMapper.updateById(po) == 1;
    }

    @Override
    public List<BlogPostCategory> selectByAuthorId(Long authorId) {
        if (authorId == null) {
            return List.of();
        }
        LambdaQueryWrapper<BlogPostCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostCategoryPO::getUserId, authorId);
        List<BlogPostCategoryPO> selectList = categoryMapper.selectList(wrapper);
        return categoryAssembler.toDomainList(selectList);
    }
}
