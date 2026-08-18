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

/**
 *
 *
 *
 * @author caldm
 */
@Repository
public class BlogPostTagRepositoryImpl implements BlogPostTagRepository {
    @Autowired
    private BlogPostTagMapper blogPostTagMapper;
    @Autowired
    private BlogPostTagAssembler blogPostTagAssembler;

    @Override
    public BlogPostTag findById(Long id) {
        BlogPostTagPO po = blogPostTagMapper.selectById(id);
        return blogPostTagAssembler.toDomain(po);
    }

    @Override
    public boolean update(BlogPostTag tag) {
        BlogPostTagPO po = blogPostTagAssembler.toPO(tag);
        return blogPostTagMapper.updateById(po) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BlogPostTagPO::getId, id).set(BlogPostTagPO::getDeleted, true);
        return blogPostTagMapper.update(wrapper) == 1;
    }

    @Override
    public boolean add(BlogPostTag tag) {
        BlogPostTagPO po = blogPostTagAssembler.toPO(tag);
        return blogPostTagMapper.insert(po) == 1;
    }

    @Override
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        LambdaUpdateWrapper<BlogPostTagPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(BlogPostTagPO::getId, ids)
                .eq(BlogPostTagPO::getDeleted, false)
                .set(BlogPostTagPO::getDeleted, true);
        return blogPostTagMapper.update(null, wrapper);
    }

    @Override
    public boolean existsByName(String name, Long authorId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<BlogPostTagPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogPostTagPO::getName, name.trim())
                .eq(BlogPostTagPO::getAuthorId, authorId);
        return blogPostTagMapper.exists(wrapper);
    }
}
