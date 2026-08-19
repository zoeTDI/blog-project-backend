package cn.caldm.www.post_context.infrastructure.repository;

import cn.caldm.www.post_context.domain.model.BlogPostCategory;
import cn.caldm.www.post_context.domain.repository.BlogPostCategoryRepository;
import cn.caldm.www.post_context.infrastructure.persistence.mapper.BlogPostCategoryMapper;
import cn.caldm.www.post_context.infrastructure.persistence.po.BlogPostCategoryPO;
import cn.caldm.www.post_context.interfaces.assembler.BlogPostCategoryAssembler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public BlogPostCategory save(BlogPostCategory category) {
        BlogPostCategoryPO po = categoryAssembler.toPO(category);
        if (po.getId() == null) {
            categoryMapper.insert(po);
        } else {
            categoryMapper.updateById(po);
        }
        return categoryAssembler.toDomain(po);
    }
}
